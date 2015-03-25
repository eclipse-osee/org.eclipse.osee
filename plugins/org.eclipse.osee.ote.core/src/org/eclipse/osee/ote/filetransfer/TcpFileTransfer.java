package org.eclipse.osee.ote.filetransfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public class TcpFileTransfer {



	private static final int SECRET_CODE = 0xF17E;
	private static final int BLOCK_SIZE = 64*1024;

	public static TcpFileTransferHandle sendFile(ExecutorService service, final File file, final InetSocketAddress address) throws IOException{
		if (!file.exists()) {
			throw new IllegalArgumentException("Invalid file specified: " + file.getAbsolutePath());
		}
		final TransferProgressMonitor monitor = new TransferProgressMonitor();
		monitor.updateTransferStartTime(System.currentTimeMillis());
		final SocketChannel sendChannel =  SocketChannel.open(address);

		final TcpFileTransfer fileTransfer = new TcpFileTransfer(Direction.FILE_TO_CHANNEL, file);
		Callable<Boolean> c = new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				fileTransfer.openSender(monitor, sendChannel);
				monitor.updateTransferCompleteTime(System.currentTimeMillis());
				return true;
			}
		};
		InetSocketAddress localAddress = (InetSocketAddress) sendChannel.socket().getLocalSocketAddress();
		return new TcpFileTransferHandle(monitor, localAddress, service.submit(c));
	}


	/**
	 * 
	 * @param service
	 * @param file
	 * @param port
	 * @return the transfer handle or null if the file to be written to is in use by another process
	 * @throws IOException
	 */
	public static TcpFileTransferHandle receiveFile(ExecutorService service, final File file, int port) throws IOException{
		if (service == null) {
			throw new NullPointerException("Service cannot be null");
		}

		final TcpFileTransfer fileTransfer;
		try {
			fileTransfer = new TcpFileTransfer(Direction.CHANNEL_TO_FILE, file);
		} catch (FileNotFoundException e) {
			// most likely due to file being used by another process
			return null;
		}
		final TransferProgressMonitor monitor = new TransferProgressMonitor();
		monitor.updateTransferStartTime(System.currentTimeMillis());

		final ServerSocketChannel channel = ServerSocketChannel.open();
		channel.configureBlocking(false);
		channel.socket().bind(new InetSocketAddress(InetAddress.getLocalHost(), port));
		InetSocketAddress localAddress = (InetSocketAddress) channel.socket().getLocalSocketAddress();

		Callable<Boolean> c = new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				fileTransfer.openReceiver(monitor, channel);
				monitor.updateTransferCompleteTime(System.currentTimeMillis());

				return true;

			}
		};
		return new TcpFileTransferHandle(monitor, localAddress, service.submit(c));
	}

	private static enum Direction {
		FILE_TO_CHANNEL {

			@Override
			long transfer(FileChannel fileChannel, SocketChannel byteChannel, long position, long count) throws IOException{
				return fileChannel.transferTo(position, count, byteChannel);
			}

			@Override
			FileChannel createChannel(File file) throws IOException{
				return new FileInputStream(file).getChannel();
			}

		},
		CHANNEL_TO_FILE {

			@Override
			long transfer(FileChannel fileChannel, SocketChannel byteChannel, long position, long count) throws IOException{
				return fileChannel.transferFrom(byteChannel, position, count);				
			}

			@Override
			FileChannel createChannel(File file) throws IOException {
				FileChannel channel = new FileOutputStream(file).getChannel();
				channel.lock();
				return channel;
			}


		};

		abstract long transfer(FileChannel fileChannel, SocketChannel byteChannel, long position, long count) throws IOException;
		abstract FileChannel createChannel(File file) throws IOException;
	}

	private final Direction direction;
	private final File file;

	private TcpFileTransfer(Direction direction, File file) throws FileNotFoundException, IOException{
		this.direction = direction;
		this.file = file;
	}


	private void openSender(TransferProgressMonitor monitor, SocketChannel sendChannel) throws IOException{		
		try {
			Selector selector = Selector.open();
			try {
				sendChannel.configureBlocking(false);
				sendChannel.register(selector, SelectionKey.OP_WRITE);
				final File[] files;
				if (file.isDirectory()) {
					files = file.listFiles();					
				} else {
					files = new File[]{file};
				}
				int fileCounter = 1;
				ByteBuffer buffer = ByteBuffer.allocate(1024);
				for (File targetFile : files) {
					long size = targetFile.length();
					monitor.updateTotalTransferAmount(size);
					buffer.clear();
					buffer.putInt(SECRET_CODE);
					buffer.putInt(files.length);
					buffer.putInt(fileCounter++);
					String name = targetFile.getName();
					buffer.putInt(name.length());
					buffer.put(name.getBytes());
					buffer.putLong(size);
					buffer.flip();
					sendChannel.write(buffer);
					transfer(monitor, sendChannel, selector, size, targetFile);
					buffer.clear();
					sendChannel.keyFor(selector).interestOps(SelectionKey.OP_READ);
					waitForData(selector, sendChannel, buffer);
					sendChannel.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
				}
			} finally {
				selector.close();
			}
		} finally {
			sendChannel.close();
		}
	}

	private void openReceiver(TransferProgressMonitor monitor, ServerSocketChannel serverChannel) throws IOException{		
		try {
			Selector selector = Selector.open();
			try {
				serverChannel.register(selector, SelectionKey.OP_ACCEPT);
				// only way to timeout on accept with channels is to use a selector. 
				// ServerSocketChannel does not honor the socket.setSoTimeout() value
				if (selector.select(10000) < 1) {
					throw new SocketTimeoutException();
				}
				selector.selectedKeys().clear();
				serverChannel.keyFor(selector).cancel();
				
				SocketChannel receiveChannel =  serverChannel.accept();
				receiveChannel.configureBlocking(false);
				receiveChannel.register(selector, SelectionKey.OP_READ);

				// now we need to handle timeouts for the data channel
				try {
					boolean done = false;
					ByteBuffer buffer = ByteBuffer.allocate(1024);
					byte[] fileNameBytes = new byte[2048];
					while (!done) {
						buffer.clear();
						// we only want to read the next 12 bytes, anything more than 12 is the file data
						// and we want that left in the channel to be consumed later
						buffer.limit(16);
	
						waitForData(selector, receiveChannel, buffer);
						
						buffer.flip();
						int code = buffer.getInt();
						if (code != SECRET_CODE) {
							throw new IOException("Unexpected data during transfer negotiation");
						}
						int fileCount = buffer.getInt();
						int currentFile = buffer.getInt();
						monitor.updateNumberOfFiles(fileCount);
						int fileNameLength = buffer.getInt();
						buffer.clear();
						buffer.limit(fileNameLength + 8);
						waitForData(selector, receiveChannel, buffer);
						buffer.flip();
						buffer.get(fileNameBytes, 0, fileNameLength);
						long fileSize = buffer.getLong();
						monitor.updateTotalTransferAmount(fileSize);
						File targetFile = file.isDirectory() ? new File(file, new String(fileNameBytes, 0, fileNameLength)) : file;
						transfer(monitor, receiveChannel, selector, fileSize, targetFile);
						buffer.clear();
						buffer.putInt(0xF0F0);
						buffer.flip();
						receiveChannel.write(buffer);
						done = currentFile == fileCount;
						
					}
				} finally {
					receiveChannel.close();
				}
			} finally {
				selector.close();
			}
		} finally {
			serverChannel.close();
		}
	}
	
	private static void waitForData(Selector selector, SocketChannel channel, ByteBuffer buffer) throws SocketTimeoutException, IOException{
		if (selector.select(10000) < 1) {
			throw new SocketTimeoutException();
		}
		selector.selectedKeys().clear();
		channel.read(buffer);
	}


	private static void waitForChannel(Selector selector) throws SocketTimeoutException, IOException{
		selector.select(10000);
		Set<SelectionKey> set = selector.selectedKeys();
		if (set.size() == 0) {
			throw new SocketTimeoutException();
		}
		set.clear();						
	}
	
	private void transfer(TransferProgressMonitor monitor, SocketChannel channel, Selector selector, long totalTransferAmount, File targetFile) throws IOException {
		// we need a selector to deal with time outs 
		FileChannel fileChannel = direction.createChannel(targetFile);
		try {
			long position = 0;
			while (position < totalTransferAmount) {
				waitForChannel(selector);
				long count = direction.transfer(fileChannel, channel, position, Math.min(BLOCK_SIZE, totalTransferAmount - position));
				if (count < 0) {
					channel.keyFor(selector).cancel();
					continue;
				}
				position += count;
				monitor.updateAmountTransferred(position);
			}
		} finally {
			fileChannel.close();
		}
	}

}
