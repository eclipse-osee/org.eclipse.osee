package org.eclipse.osee.ote.filetransfer;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TransferProgressMonitor {

	private final AtomicInteger numberOfFiles = new AtomicInteger(-1);
	private final AtomicLong total = new AtomicLong(-1L);
	private final AtomicLong current = new AtomicLong(0L);
	private final AtomicLong transferStartTime = new AtomicLong(-1L);
	private final AtomicLong transferCompleteTime = new AtomicLong(-1L);

	public long getAmountTransferred() {
		return current.get();
	}
	
	public long getTotalTransferAmount() {
		return total.get();
	}
	
	public long getPercentTransferred() {
		return (current.get() * 100) / total.get();
	}
	
	void updateAmountTransferred(long amount) {
		current.set(amount);
	}
	
	void updateTotalTransferAmount(long totalAmount) {
		total.set(totalAmount);				
	}
	
	void updateNumberOfFiles(int files) {
		numberOfFiles.set(files);
	}

	public int getNumberOfFiles() {
		return numberOfFiles.get();
	}

	public long getTransferStartTime() {
		return transferStartTime.get();
	}

	public long getTransferCompleteTime() {
		return transferCompleteTime.get();
	}
	
	void updateTransferStartTime(long time) {
		transferStartTime.set(time);
	}
	
	void updateTransferCompleteTime(long time) {
		transferCompleteTime.set(time);
	}
	
	public long getTotalTransferTime() {
		return getTransferCompleteTime() - getTransferStartTime();
	}
}
