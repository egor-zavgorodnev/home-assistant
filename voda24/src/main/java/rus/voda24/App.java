package rus.voda24;

import rus.voda24.exceptions.NetException;
import rus.voda24.service.Voda24Service;

public class App {
    public static void main(String[] args) throws NetException {
        Voda24Service service = new Voda24Service();

        System.out.println(service.getBalance());

    }
}
