public class Main {

    public static void main (String[] args) throws Exception {

        System.out.println ("Hello world!");
        OS myOs = new OS ();
        IoClass ioClass = new IoClass ();
        ioClass.start ();
        UseOs useOs = new UseOs ();
        useOs.start ();
//        myOs.start ();
//        myOs.run ();

    }
}