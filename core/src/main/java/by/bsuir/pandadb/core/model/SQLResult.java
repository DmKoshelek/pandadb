package by.bsuir.pandadb.core.model;

public class SQLResult {
    private SQLResultType result;

    private String mesage;

    public SQLResult() {
    }

    public SQLResult(SQLResultType result, String mesage) {
        this.result = result;
        this.mesage = mesage;
    }

    public SQLResultType getResult() {
        return result;
    }

    public void setResult(SQLResultType result) {
        this.result = result;
    }

    public String getMesage() {
        return mesage;
    }

    public void setMesage(String mesage) {
        this.mesage = mesage;
    }


}
