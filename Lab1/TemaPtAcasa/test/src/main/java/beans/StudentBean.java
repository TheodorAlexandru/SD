package beans;

public class StudentBean implements java.io.Serializable {
    private int id;
    private String nume = null;
    private String prenume = null;
    private int varsta = 0;
    private int an_nastere = 0;

    public StudentBean() {
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getPrenume() {
        return prenume;
    }
    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }
    public int getVarsta() {
        return varsta;
    }
    public void setVarsta(int varsta) {
        this.varsta = varsta;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getAn_nastere(){
        return an_nastere;
    }

    public void setAAn_nastere(int an_nastere){
        this.an_nastere = an_nastere;
    }
}
