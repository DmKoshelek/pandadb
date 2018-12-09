package by.bsuir.pandadb.core.model;

public class DBComparedValue extends DBValue {

    public enum COMPATORS {
        EQUALS("="), NOT_EQUALS("!="),
        LESS("<"), MORE(">"),
        LESS_OR_EQUALS("<="),
        MORE_OR_EQUALS(">=");

        private String sign;

        COMPATORS(String sign) {
            this.sign = sign;
        }

        public String getSign() {
            return sign;
        }
    }

    private COMPATORS compator;

    public COMPATORS getCompator() {
        return compator;
    }

    public void setCompator(COMPATORS compator) {
        this.compator = compator;
    }
}
