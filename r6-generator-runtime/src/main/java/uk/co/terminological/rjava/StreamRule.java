package uk.co.terminological.rjava;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public interface StreamRule<Z,W> extends Rule<Z> {

	Function<Z,Stream<W>> streamRule();
	List<MapRule<W>> mapRules();
	
}
