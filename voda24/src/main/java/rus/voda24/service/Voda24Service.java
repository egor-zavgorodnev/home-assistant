package rus.voda24.service;

import rus.voda24.exceptions.NetException;
import rus.voda24.repository.DataReceiver;

import java.io.IOException;

public class Voda24Service {

    public String getBalance() throws NetException {
        DataReceiver dataReceiver = new DataReceiver();

        try {
            return DataParser.getBalanceFromJson(dataReceiver.getAccountInfo());
        } catch (IOException e) {
            throw new NetException("Problem with data retrieving");
        }
    }
}
