/*********************************************************************
 * Copyright (c) 2026 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.rest.internal;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * There may be trouble with concurrency in the Word COM object that this class calls. It should only be called
 * serially.
 *
 * @author David W. Miller
 */
public class WmzConverter {
   private static final String POWERSHELL_EXE = "powershell.exe";
   private static final Duration SCRIPT_TIMEOUT = Duration.ofMinutes(2);
   private static final Duration OUTPUT_READER_WAIT = Duration.ofSeconds(10);

   private final XResultData result = new XResultData();
   // dependencies assumed available in the class
   private final OrcsApi orcsApi;

   public WmzConverter(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public XResultData convertWMZChildAttribute(BranchId branch, ArtifactId artifact, String scriptPath) {
      XResultData validate = validateInputs(branch, artifact, scriptPath, result);
      if (validate.isErrors()) {
         return validate;
      }

      try {
         ArtifactReadable resolvedArt = orcsApi.getQueryFactory().fromBranch(branch).andId(artifact).asArtifact();

         String extension = resolvedArt.getSoleAttributeAsString(CoreAttributeTypes.Extension);
         if (!("emz".equals(extension) || "wmz".equals(extension))) {
            result.error("Wrong content type, content must be emz or wmz native content");
            return result;
         }
         InputStream nativeContent = resolvedArt.getSoleAttributeValue(CoreAttributeTypes.NativeContent);

         if (nativeContent == null) {
            String msg = "No native content available for artifact " + artifact.getIdString();
            result.error(msg);
            return result;
         }

         try {
            return withTempDir(tempDir -> {
               Path inputFile = tempDir.resolve("current." + extension);
               Path outputFolder = tempDir.resolve("output");
               Files.createDirectories(outputFolder);

               Files.write(inputFile, Lib.inputStreamToBytes(nativeContent));

               int exitCode = 0;
               try {
                  exitCode = runPowerShellConversion(scriptPath, tempDir, outputFolder, SCRIPT_TIMEOUT);
               } catch (InterruptedException ex) {
                  result.error(ex.toString());
                  exitCode = 100;
               }

               if (exitCode != 0) {
                  String msg = String.format("PowerShell script exited with code %d for artifact %s", exitCode,
                     artifact.getIdString());
                  result.error(msg);
                  return result;
               }

               Optional<Path> produced = findFirstFile(outputFolder);
               if (!produced.isPresent()) {
                  String msg = "Couldn't find output from script for artifact " + artifact.getIdString();
                  result.warning(msg);
                  return result;
               }

               try (InputStream pngIs = Files.newInputStream(produced.get(), StandardOpenOption.READ)) {
                  replaceArtifactContent(branch, resolvedArt, pngIs);
               }
               return result;
            });

         } catch (IOException e) {
            result.error("IO error during conversion");
            return result;
         } catch (RuntimeException e) {
            result.error("Conversion failed");
            return result;
         }
      } catch (Exception e) {
         result.errorf("Could not resolve artifact because %s", e.getMessage());
         return result;
      }
   }

   private XResultData validateInputs(BranchId branch, ArtifactId artifact, String scriptPath, XResultData rerr) {
      if (branch == null || branch.isInvalid()) {
         rerr.error("Invalid branch provided");
      }
      if (artifact == null || artifact.isInvalid()) {
         rerr.error("Invalid artifact provided");
      }
      if (Strings.isInvalid(scriptPath)) {
         rerr.error("Invalid script path provided");
      }
      return rerr;
   }

   private XResultData withTempDir(TempDirCallback callback) throws IOException {
      Path tempDir = Files.createTempDirectory("wmz_convert_");
      try {
         return callback.apply(tempDir);
      } finally {
         // Best-effort cleanup; on Windows locked files may remain
         try (Stream<Path> s = Files.walk(tempDir)) {
            s.sorted(Comparator.reverseOrder()).forEach(p -> {
               try {
                  Files.deleteIfExists(p);
               } catch (IOException ex) {
                  // log and continue
                  result.log("Failed to delete temp file: " + p + " : " + ex.getMessage());
               }
            });
         } catch (IOException e) {
            result.log("Failed walking temp dir for cleanup: " + e.getMessage());
         }
      }
   }

   private Optional<Path> findFirstFile(Path folder) throws IOException {
      try (Stream<Path> s = Files.list(folder)) {
         return s.findFirst();
      }
   }

   private int runPowerShellConversion(String scriptPath, Path inputFolder, Path outputFolder, Duration timeout)
      throws IOException, InterruptedException {
      List<String> command = new ArrayList<>();
      command.add(POWERSHELL_EXE);
      command.add("-NoProfile");
      command.add("-ExecutionPolicy");
      command.add("Bypass");
      command.add("-File");
      command.add(scriptPath);
      command.add("-InputFolder");
      command.add(inputFolder.toString());
      command.add("-OutputFolder");
      command.add(outputFolder.toString());

      ProcessBuilder pb = new ProcessBuilder(command);
      pb.redirectErrorStream(true);
      Process proc = pb.start();

      ExecutorService ex = Executors.newSingleThreadExecutor();
      Future<?> reader = drainStreamAsync(proc.getInputStream(), ex);
      if (reader == null) {
         /*
          * The reader is intentionally null - uncomment the code in drainStreamAsync to use this conversion. The code
          * is commented out since the converter is only intended for developer use.
          */
         proc.destroyForcibly();
         return 1000;
      }

      boolean finished = proc.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS);
      if (!finished) {
         proc.destroyForcibly();
         reader.cancel(true);
         ex.shutdownNow();
         throw new RuntimeException("Conversion script timed out");
      }

      try {
         reader.get(OUTPUT_READER_WAIT.toMillis(), TimeUnit.MILLISECONDS);
      } catch (Exception e) {
         // best-effort: ignore or log
         result.log("Output reader finished with error/timeout: " + e.getMessage());
      } finally {
         ex.shutdownNow();
      }

      return proc.exitValue();
   }

   /*
    * To use this method and allow the converter to run, replace the null return statement with the commented out code.
    */
   private Future<?> drainStreamAsync(InputStream is, ExecutorService ex) {
      return null;
      //      return ex.submit(() -> {
      //         try (
      //            BufferedReader r = new BufferedReader(new InputStreamReader(is, java.nio.charset.StandardCharsets.UTF_8))) {
      //            String line;
      //            while ((line = r.readLine()) != null) {
      //               result.log("[WMZ-CONV] " + line);
      //            }
      //         } catch (IOException e) {
      //            result.log("Error reading process output: " + e.getMessage());
      //         }
      //      });
   }

   private void replaceArtifactContent(BranchId branch, ArtifactReadable resolvedArt, InputStream pngIs) {
      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch,
         orcsApi.userService().getUserOrSystem(), "EMZ/WMZ conversion replaces image attributes with png content");

      tx.setSoleAttributeValue(resolvedArt, CoreAttributeTypes.NativeContent, pngIs);
      tx.setSoleAttributeValue(resolvedArt, CoreAttributeTypes.Extension, "png");
      TransactionToken txToken = tx.commit();
      if (txToken == null) {
         result.error("Transaction did not commit properly in WMZ Convert");
      } else {
         result.setTxId(txToken.getIdString());
         result.success("Completed replacement of wmz/emz content with png content, tx:%s", txToken.getIdString());
      }
   }

   @FunctionalInterface
   private interface TempDirCallback {
      XResultData apply(Path tempDir) throws IOException;
   }
}
