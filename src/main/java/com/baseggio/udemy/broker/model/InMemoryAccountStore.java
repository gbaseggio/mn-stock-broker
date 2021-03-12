package com.baseggio.udemy.broker.model;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.UUID;

@Singleton
public class InMemoryAccountStore {

    private final HashMap<UUID, WatchList> watchListsPerAccount = new HashMap<>();

    public WatchList getWatchList(UUID accountID) {
        return watchListsPerAccount.getOrDefault(accountID, new WatchList());
    }

    public WatchList updateWatchList(UUID accountId, WatchList watchList) {
        watchListsPerAccount.put(accountId, watchList);
        return getWatchList(accountId);
    }

    public void deleteWatchList(UUID accountId) {
        watchListsPerAccount.remove(accountId);
    }
}
