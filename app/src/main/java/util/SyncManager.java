package util;

import java.util.Observable;


public class SyncManager extends Observable {

	private static SyncManager instance = new SyncManager();

	public static SyncManager getInstance() {
		return instance;
	}

	private SyncManager() {
	}

	private boolean syncInProgress = false;
	/*
	public void sync(final String param1, final String param2, final int param3){
        
        if (syncInProgress)
            return;
        
        // example of a custom error message
        if (param2.equals("")) {
            setChanged();
            notifyObservers(new SyncUpdateMessage(SyncUpdateMessage.SYNC_CUSTOM_ERROR,null));
            return;
        }
        
        // set flag
        syncInProgress = true;
        notifyObservers(new SyncUpdateMessage(SyncUpdateMessage.SYNC_STARTED, null));
        
        // all good, begin process
        new Thread(new Runnable() {
            @Override
            public void run() {
                Note note = databaseManager.getNote(String param1);
                String result = NoteUploader.uploadNote(note);

                setChanged();
                notifyObservers(new SyncUpdateMessage(
                        SyncUpdateMessage.SYNC_SUCCESSFUL, result));

                // release flag
                syncInProgress = false;
            }
        }).start();
    }
    */
	public void testNotify(int messageCode, String idx){
        setChanged();
		notifyObservers(new SyncUpdateMessage(messageCode, idx));
	}
}