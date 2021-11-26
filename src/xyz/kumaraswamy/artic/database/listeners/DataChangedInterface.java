package xyz.kumaraswamy.artic.database.listeners;


public interface DataChangedInterface {
    void whenNewMessage(String key, String value);
}
