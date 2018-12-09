package by.bsuir.pandadb.core.types.comparators;

import by.bsuir.pandadb.core.model.DBComparedValue;
import by.bsuir.pandadb.core.model.DBValue;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;


@Component("defaultDBComparator")
public class DefaultDBTypeComparator implements DBTypeComparator {

    public int compare(DBValue first, DBValue second) {
        Comparable firstValue = (Comparable) first.getValue();
        Comparable secondValue = (Comparable) second.getValue();
        return firstValue.compareTo(secondValue);
    }

    private static final DBComparedValue.COMPATORS[] LESS_COMPARATORS =
            {DBComparedValue.COMPATORS.LESS,
                    DBComparedValue.COMPATORS.LESS_OR_EQUALS,
                    DBComparedValue.COMPATORS.NOT_EQUALS};

    private static final DBComparedValue.COMPATORS[] MORE_COMPARATORS =
            {DBComparedValue.COMPATORS.MORE,
                    DBComparedValue.COMPATORS.MORE_OR_EQUALS,
                    DBComparedValue.COMPATORS.NOT_EQUALS};

    private static final DBComparedValue.COMPATORS[] EQUALS_COMPARATORS =
            {DBComparedValue.COMPATORS.EQUALS,
                    DBComparedValue.COMPATORS.MORE_OR_EQUALS,
                    DBComparedValue.COMPATORS.LESS_OR_EQUALS};

    @Override
    public boolean compareByComparedValue(DBValue value, DBComparedValue comparedValue) {
        int compared = compare(value, comparedValue);

        DBComparedValue.COMPATORS compator = comparedValue.getCompator();

        boolean lessResult = (compared < 0) && (ArrayUtils.contains(LESS_COMPARATORS, compator));
        boolean moreResult = (compared > 0) && (ArrayUtils.contains(MORE_COMPARATORS, compator));
        boolean equalsResult = (compared == 0) && (ArrayUtils.contains(EQUALS_COMPARATORS, compator));

        return lessResult || moreResult || equalsResult;
    }
}
