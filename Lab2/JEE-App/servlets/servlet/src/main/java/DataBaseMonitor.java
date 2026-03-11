import ejb.StudentEntity;
import javax.persistence.*;
import java.util.List;

public class DataBaseMonitor implements Runnable{
    private int varstaMin = 18;
    private int varstaMax = 65;

    private int idMin = 1;
    private int idMax = 50;

    public static String alarmMessage = null;

    @Override
    public void run()
    {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("bazaDeDateSQLite");
        while(true) {
            resetMessage();

            EntityManager em = factory.createEntityManager();

            List<StudentEntity> students = em.createQuery("SELECT s from StudentEntity s", StudentEntity.class).getResultList();
            for (StudentEntity student : students)
            {
                if (student.getVarsta() < varstaMin || student.getVarsta() > varstaMax)
                {
                    alarmMessage = "Eroare la studentul " + student.getNume() + " " + student.getPrenume() + ": Varsta trebuie sa fie intre " + varstaMin + " si " + varstaMax + ", dar are de fapt " + student.getVarsta();
                }
                else if (student.getId() < idMin || student.getId() > idMax)
                {
                    alarmMessage = "Eroare la studentul " + student.getNume() + " " + student.getPrenume() + ": ID-ul trebuie sa fie intre " + idMin + " si " + idMax + ", dar e de fapt " + student.getId();
                }
            }

            em.close();

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                break;
            }

        }

        if (factory != null && factory.isOpen()) {
            factory.close();
        }

    }

    public void resetMessage(){
        alarmMessage = null;
    }
}
