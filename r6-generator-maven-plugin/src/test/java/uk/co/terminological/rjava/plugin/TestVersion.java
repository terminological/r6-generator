package uk.co.terminological.rjava.plugin;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TestVersion {

	void doTest(String javaVersion, String rVersion, boolean bidir) {
		
		Version v1 = new Version(javaVersion);
		Version v2 = new Version(rVersion);
		
		System.out.println(v1.javaVersion()+" -> "+v1.rVersion());
		System.out.println(v2.rVersion()+" -> "+v1.javaVersion());
		
		assertEquals(v1.javaVersion(), javaVersion);
		if (bidir) assertEquals(v1.rVersion(), rVersion);
		
		if (bidir) assertEquals(v2.javaVersion(), javaVersion);
		assertEquals(v2.rVersion(), rVersion);
		
		if (bidir) assertEquals(v1,v2);
	}

	@Test
	void test() {
		doTest("0.1.0","0.1.0", true);
		doTest("0.1.0-SNAPSHOT","0.0.99.9000", true);
		doTest("1.0.0-SNAPSHOT","0.99.99.9000", true);
		doTest("0.0.1-SNAPSHOT","0.0.0.9000", true);
		doTest("0.1.0-SNAPSHOT","0.0.99.9000", true);
		doTest("1.2.3-SNAPSHOT","1.2.2.9000", true);
		doTest("1.2.3-SNAPSHOT","1.2.2.9002", false);
	}
	
	void doTestIncrement(String version, String nextPatch, String nextMinor, String nextMajor) {
		
		System.out.println(version+" -> "+
				new Version(version).incrementPatch()+" | "+
				new Version(version).incrementMinor()+" | "+
				new Version(version).incrementMajor()
		);
		
		assertEquals(nextPatch, new Version(version).incrementPatch().javaVersion());
		assertEquals(nextMinor, new Version(version).incrementMinor().javaVersion());
		assertEquals(nextMajor, new Version(version).incrementMajor().javaVersion());
		
	}
	
	@Test
	void testIncrement() {
		doTestIncrement("0.1.0","0.1.1","0.2.0","1.0.0");
		doTestIncrement("0.1.0.9000","0.1.1","0.2.0","1.0.0");
		doTestIncrement("1.2.3","1.2.4","1.3.0","2.0.0");
		
		
	}
}
