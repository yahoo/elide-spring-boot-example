package example.datastore;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.data.domain.KeysetScrollPosition;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.domain.Window;
import org.springframework.data.jpa.domain.Specification;

import com.yahoo.elide.core.Path;
import com.yahoo.elide.core.RequestScope;
import com.yahoo.elide.core.datastore.DataStoreIterable;
import com.yahoo.elide.core.datastore.DataStoreTransaction;
import com.yahoo.elide.core.filter.expression.AndFilterExpression;
import com.yahoo.elide.core.filter.expression.FilterExpression;
import com.yahoo.elide.core.filter.expression.FilterExpressionVisitor;
import com.yahoo.elide.core.filter.expression.NotFilterExpression;
import com.yahoo.elide.core.filter.expression.OrFilterExpression;
import com.yahoo.elide.core.filter.predicates.FilterPredicate;
import com.yahoo.elide.core.request.EntityProjection;
import com.yahoo.elide.core.request.Pagination;
import com.yahoo.elide.core.request.Pagination.Direction;
import com.yahoo.elide.core.request.Sorting;
import com.yahoo.elide.core.request.Sorting.SortOrder;

import example.service.QueryService;
import example.service.SpringDataCursorEncoder;

/**
 * {@link DataStoreTransaction} for the {@link SpringDataDataStore}.
 * <p>
 * This is for demonstration purposes only.
 */
public class SpringDataDataStoreTransaction implements DataStoreTransaction {
    private final QueryService queryService;
    private final SpringDataCursorEncoder cursorEncoder;

    public SpringDataDataStoreTransaction(QueryService queryService, SpringDataCursorEncoder cursorEncoder) {
        this.queryService = queryService;
        this.cursorEncoder = cursorEncoder;
    }

    @Override
    public void close() throws IOException {
        // Read only
    }

    @Override
    public <T> void save(T entity, RequestScope scope) {
        // Read only
    }

    @Override
    public <T> void delete(T entity, RequestScope scope) {
        // Read only
    }

    @Override
    public void flush(RequestScope scope) {
        // Read only
    }

    @Override
    public void commit(RequestScope scope) {
        // Read only
    }

    @Override
    public <T> void createObject(T entity, RequestScope scope) {
        // Read only
    }

    @Override
    public <T> DataStoreIterable<T> loadObjects(EntityProjection entityProjection, RequestScope scope) {
        Pagination pagination = entityProjection.getPagination();
        int limit = pagination.getLimit();
        Direction direction = pagination.getDirection();
        boolean cursorPagination = direction != null;
        ScrollPosition scrollPosition;
        if (cursorPagination) {
            // Cursor Pagination
            String cursor = pagination.getCursor();
            scrollPosition = getScrollPosition(direction, cursor);
        } else {
            // Offset Pagination
            if (pagination.getOffset() == 0) {
                // Start of scroll operation as offset 0 skips the first element
                scrollPosition = ScrollPosition.offset();
            } else {
                scrollPosition = ScrollPosition.offset(pagination.getOffset() - 1);
            }
        }
        Sort sort = getSort(entityProjection.getSorting());
        FilterExpression filterExpression = entityProjection.getFilterExpression();
        Specification<?> specification = getSpecification(filterExpression);
        Class<?> entityClass = entityProjection.getType().getUnderlyingClass().get();
        Window<?> result = this.queryService.query(entityClass, specification, sort, Limit.of(limit),
                scrollPosition);
        if (pagination.returnPageTotals()) {
            pagination.setPageTotals(this.queryService.count(entityClass, specification));
        }
        int size = result.size();
        // Determine startCursor and endCursor
        if (result.size() > 0 && cursorPagination) {
            // Spring Data can only determine if there are more records following in the
            // same direction
            if (Direction.BACKWARD.equals(direction)) {
                pagination.setHasPreviousPage(result.hasNext());
            } else {
                pagination.setHasNextPage(result.hasNext());
            }

            KeysetScrollPosition start = null;
            KeysetScrollPosition end = null;
            start = (KeysetScrollPosition) result.positionAt(0);
            if (size == 1) {
                end = start;
            } else if (size > 1) {
                end = (KeysetScrollPosition) result.positionAt(size - 1);
            }
            if (start != null) {
                Map<String, Object> startKeys = start.getKeys();
                String startCursor = cursorEncoder.encode(startKeys);
                pagination.setStartCursor(startCursor);
            }
            if (end != null) {
                Map<String, Object> endKeys = end.getKeys();
                String endCursor = cursorEncoder.encode(endKeys);
                pagination.setEndCursor(endCursor);
            }
        }
        return new DataStoreIterable<T>() {
            @SuppressWarnings("unchecked")
            @Override
            public Iterable<T> getWrappedIterable() {
                return (Iterable<T>) result.getContent();
            }
        };
    }

    /**
     * Gets the specification based on the filter expression.
     * 
     * @param filterExpression the filter expression
     * @return the specification
     */
    private Specification<?> getSpecification(FilterExpression filterExpression) {
        Specification<?> specification = Specification.where(null);
        if (filterExpression != null) {
            SpecificationQueryVisitor visitor = new SpecificationQueryVisitor();
            specification = filterExpression.accept(visitor);
        }
        return specification;
    }

    /**
     * Gets the sort based on the sorting specified.
     *
     * @param sorting the sort
     * @return the sort
     */
    private Sort getSort(Sorting sorting) {
        Sort sort = Sort.unsorted();
        if (sorting != null && !sorting.isDefaultInstance()) {
            List<Order> orders = new ArrayList<>();
            for (Entry<Path, SortOrder> entry : sorting.getSortingPaths().entrySet()) {
                switch (entry.getValue()) {
                case asc:
                    orders.add(Order.asc(entry.getKey().getFieldPath()));
                    break;
                case desc:
                    orders.add(Order.desc(entry.getKey().getFieldPath()));
                    break;
                }
            }
            sort = Sort.by(orders);
        }
        return sort;
    }

    /**
     * Gets the scroll position based on the direction and cursor.
     * 
     * @param direction forward or backward
     * @param cursor    the encoded cursor
     * @return the scroll position
     */
    private KeysetScrollPosition getScrollPosition(Direction direction, String cursor) {
        KeysetScrollPosition keysetScrollPosition = null;
        Map<String, ?> keys = cursorEncoder.decode(cursor);
        if (Direction.FORWARD.equals(direction)) {
            keysetScrollPosition = ScrollPosition.forward(keys);
        } else {
            keysetScrollPosition = ScrollPosition.backward(keys);
        }
        return keysetScrollPosition;
    }

    @Override
    public void cancel(RequestScope scope) {
        // Read only
    }
}

/**
 * Creates the Spring Data {@link Specification} based on Elide's
 * {@link FilterExpression}.
 * <p>
 * This is for demonstration purposes only.
 */
@SuppressWarnings("rawtypes")
class SpecificationQueryVisitor implements FilterExpressionVisitor<Specification> {
    @Override
    public Specification<?> visitPredicate(FilterPredicate filterPredicate) {
        return (root, query, builder) -> {
            switch (filterPredicate.getOperator()) {
            case IN:
                return root.get(filterPredicate.getField()).in(filterPredicate.getValues());
            case IN_INSENSITIVE:
                return builder.lower(root.get(filterPredicate.getField()))
                        .in(filterPredicate.getValues()
                                .stream()
                                .map(Object::toString)
                                .map(String::toLowerCase)
                                .toList());
            case NOT:
                return builder.not(root.get(filterPredicate.getField()).in(filterPredicate.getValues()));
            case NOT_INSENSITIVE:
                return builder.not(builder.lower(root.get(filterPredicate.getField()))
                        .in(filterPredicate.getValues()
                                .stream()
                                .map(Object::toString)
                                .map(String::toLowerCase)
                                .toList()));
            case PREFIX_CASE_INSENSITIVE:
                return builder.like(builder.lower(root.get(filterPredicate.getField())),
                        filterPredicate.getValues().get(0).toString().toLowerCase() + "%");
            case NOT_PREFIX_CASE_INSENSITIVE:
                return builder.not(builder.like(builder.lower(root.get(filterPredicate.getField())),
                        filterPredicate.getValues().get(0).toString().toLowerCase() + "%"));
            case PREFIX:
                return builder.like(root.get(filterPredicate.getField()),
                        filterPredicate.getValues().get(0).toString() + "%");
            case NOT_PREFIX:
                return builder.not(builder.like(root.get(filterPredicate.getField()),
                        filterPredicate.getValues().get(0).toString() + "%"));
            case POSTFIX:
                return builder.like(root.get(filterPredicate.getField()),
                        "%" + filterPredicate.getValues().get(0).toString());
            case NOT_POSTFIX:
                return builder.not(builder.like(root.get(filterPredicate.getField()),
                        "%" + filterPredicate.getValues().get(0).toString()));
            case POSTFIX_CASE_INSENSITIVE:
                return builder.like(builder.lower(root.get(filterPredicate.getField())),
                        "%" + filterPredicate.getValues().get(0).toString().toLowerCase());
            case NOT_POSTFIX_CASE_INSENSITIVE:
                return builder.not(builder.like(builder.lower(root.get(filterPredicate.getField())),
                        "%" + filterPredicate.getValues().get(0).toString().toLowerCase()));
            case INFIX:
                return builder.like(root.get(filterPredicate.getField()),
                        "%" + filterPredicate.getValues().get(0).toString() + "%");
            case NOT_INFIX:
                return builder.not(builder.like(root.get(filterPredicate.getField()),
                        "%" + filterPredicate.getValues().get(0).toString() + "%"));
            case INFIX_CASE_INSENSITIVE:
                return builder.like(builder.lower(root.get(filterPredicate.getField())),
                        "%" + filterPredicate.getValues().get(0).toString().toLowerCase() + "%");
            case NOT_INFIX_CASE_INSENSITIVE:
                return builder.not(builder.like(builder.lower(root.get(filterPredicate.getField())),
                        "%" + filterPredicate.getValues().get(0).toString().toLowerCase() + "%"));
            case ISNULL:
                return root.get(filterPredicate.getField()).isNull();
            case NOTNULL:
                return root.get(filterPredicate.getField()).isNotNull();
            case LT:
                return builder.lt(root.get(filterPredicate.getField()),
                        new BigDecimal(filterPredicate.getValues().get(0).toString()));
            case LE:
                return builder.le(root.get(filterPredicate.getField()),
                        new BigDecimal(filterPredicate.getValues().get(0).toString()));
            case GT:
                return builder.gt(root.get(filterPredicate.getField()),
                        new BigDecimal(filterPredicate.getValues().get(0).toString()));
            case GE:
                return builder.ge(root.get(filterPredicate.getField()),
                        new BigDecimal(filterPredicate.getValues().get(0).toString()));
            case TRUE:
                return builder.isTrue(root.get(filterPredicate.getField()));
            case FALSE:
                return builder.isFalse(root.get(filterPredicate.getField()));
            default:
                break;
            }
            // Unsupported
            throw new IllegalArgumentException("Unsupported operator " + filterPredicate.getOperator());
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public Specification<?> visitAndExpression(AndFilterExpression expression) {
        return expression.getLeft().accept(this).and(expression.getRight().accept(this));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Specification<?> visitOrExpression(OrFilterExpression expression) {
        return expression.getLeft().accept(this).or(expression.getRight().accept(this));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Specification<?> visitNotExpression(NotFilterExpression expression) {
        return Specification.not(expression.accept(this));
    }
}
