package uk.co.terminological.rjava.threads;

public class RProgress {

	private int target = -1;
	private int progress = 0;
	private String name;
	
	public void setTarget(int target) {
		this.target = target;
	}
	
	public void increment() {
		this.progress += 1;
	}
	
	public void increment(int i) {
		progress += i;
	}
	
	public String toString() {
		if (this.target != -1) {
			double tmp = ((double) progress)/target*100;
			return String.format("%d/%d (%.0f%%)", progress, target, tmp);
		} else {
			if (progress == 0) return "in progress...";
			return "step "+progress+" complete";
		}
	}
	
	public String rCode() {
		if (name==null) return("NULL");
		if (target == -1) {
			return String.format("list(total=NA, set=%d, id=\"%s\")", this.progress, this.name);
		}
		return String.format("list(total=%d, set=%d, id=\"%s\")", this.target, this.progress, this.name);
	}

	public void setName(String name) {
		this.name = name;
	}

	public void complete() {
		progress = (target == -1) ? progress : target;
		target = progress;
	}

}
