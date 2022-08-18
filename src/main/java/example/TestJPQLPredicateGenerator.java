package example;

import java.util.function.Function;

import com.yahoo.elide.core.Path;
import com.yahoo.elide.core.filter.predicates.FilterPredicate;
import com.yahoo.elide.datastores.jpql.filter.JPQLPredicateGenerator;

public class TestJPQLPredicateGenerator implements JPQLPredicateGenerator {
	
	@Override
	public String generate(FilterPredicate predicate, Function<Path, String> aliasGenerator) {
		// TODO Auto-generated method stub
		String columnAlias = aliasGenerator.apply(predicate.getPath());
		return String.format("1  = 1 and %s in (%s)", columnAlias, predicate.getParameters().get(0).getPlaceholder());
	}

}
