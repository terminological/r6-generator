package uk.co.terminological.rjava.plugin;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Version implements Comparable<Version> {
	public int[] numbers;
	
	boolean isSnapshot() {
		return numbers[3] > 0;
	};
	

	public Version(String version) {
		final String split[] = version.split("\\-")[0].split("\\.");
		numbers = new int[4];
		for (int i = 0; i < Math.min(split.length,4); i++) {
			numbers[i] = Integer.valueOf(split[i]);
		}
		if (version.endsWith("-SNAPSHOT")) {
			numbers[3] = 9000;
			this.decrementPatch();
		} else {
			if (numbers[3] > 0 && numbers[3] < 9000) {
			numbers[3] = 9000;
		}}
	}
	
	public Version(boolean isSnapshot, int... numbers) {
		this(
			Arrays.stream(numbers).mapToObj(i -> ""+i).collect(Collectors.joining("."))+
				(isSnapshot ? "-SNAPSHOT" : "")
		);
	}
	
	public Version(int... numbers) {
		this(false, numbers);
	}

	@Override
	public int compareTo(Version another) {
		final int maxLength = Math.max(numbers.length, another.numbers.length);
		for (int i = 0; i < maxLength; i++) {
			final int left = i < numbers.length ? numbers[i] : 0;
			final int right = i < another.numbers.length ? another.numbers[i] : 0;
			if (left != right) {
				return left < right ? -1 : 1;
			}
		}
		return 0;
	}
	
	public String javaVersion() {
		if (isSnapshot()) {
			return new Version(false, numbers).incrementPatch()+(isSnapshot() ? "-SNAPSHOT": "");
		} else {
			return baseVersion();
		}
	}

	public String toString() {return rVersion();}
	
	public String rVersion() {
		if (isSnapshot()) {
			return baseVersion()+"."+numbers[3];
		} else {
			return baseVersion();
		}
	}
	
	public String baseVersion() {
		return Arrays.stream(Arrays.copyOfRange(numbers, 0, 3)).mapToObj(i -> ""+i).collect(Collectors.joining("."));
	}
	
	public Version decrementPatch() {
		if (numbers[2] == 0) {
			decrementMinor();
			numbers[2] = 99;
		} else {
			numbers[2] = numbers[2]-1;
		}
		return this;
	}
	
	public Version decrementMinor() {
		if (numbers[1] == 0) {
			decrementMajor();
			numbers[1] = 99;
		} else {
			numbers[1] = numbers[1]-1;
		}
		return this;
	}
	
	public Version decrementMajor() {
		if (numbers[0] == 0) {
			// can't decrease
		} else {
			numbers[0] = numbers[0]-1;
		}
		return this;
	}
	
	public Version decrementDev() {
		if (numbers[3] > 0 && numbers[3] < 9001) {
			numbers[3] = 0;
		} else if (numbers[3] == 0) {
			decrementPatch();
			numbers[3] = 9999;
		} else {
			numbers[3] = numbers[3]-1;
		}
		return this;
	}
	
	public Version incrementPatch() {
		if (numbers[2] >= 99) {
			incrementMinor();
			numbers[2] = 0;
		} else {
			numbers[2] = numbers[2]+1;
		}
		numbers[3] = 0;
		return this;
	}
	
	public Version incrementMinor() {
		if (numbers[1] >= 99) {
			incrementMajor();
			numbers[1] = 0;
		} else {
			numbers[1] = numbers[1]+1;
		}
		numbers[2] = 0;
		numbers[3] = 0;
		return this;
	}
	
	public Version incrementMajor() {
		numbers[0] = numbers[0]+1;
		numbers[1] = 0;
		numbers[2] = 0;
		numbers[3] = 0;
		return this;
	}
	
	public boolean equals(Object v2) {
		if (!(v2 instanceof Version)) return false;
		Version tmp = (Version) v2;
		return 
			numbers[1] == tmp.numbers[1] &&
			numbers[2] == tmp.numbers[2] &&
			numbers[3] == tmp.numbers[3] &&
			numbers[0] == tmp.numbers[0];
	}
	
}