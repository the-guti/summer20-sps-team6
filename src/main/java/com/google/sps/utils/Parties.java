package com.google.sps.utils;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.sps.media.PartySongPlayer;

// TODO don't use HashMap to store Party Players
public class Parties {
    private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private static Map<Long, PartySongPlayer> partySongPlayers = new ConcurrentHashMap<>();

    /**
     * Queries Datastore for a User Entity with the given Id, returns null if no match
     */
    public static Entity getPartyEntity(long partyId) {

        Key partyKey = KeyFactory.createKey("party", partyId);
        try {
            return datastore.get(partyKey);
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    public static boolean isPartyCreated(long partyId) {
        return getPartyEntity(partyId) != null;
    }

    public static boolean isPartyPlayerCreated(long partyId) {
        return partySongPlayers.containsKey(partyId);
    }

    /**
     * @return the partyId's current player or null if there is either no party with the given id or no PartySongPlayer
     */
    public static PartySongPlayer getPartySongPlayer(long partyId) {
        return partySongPlayers.get(partyId);
    }

    /**
     * Creates new PartySongPlayer for the party of given ID replacing the old player
     */
    public static void createOrReplacePartySongPlayer(long partyId) {
        partySongPlayers.put(partyId, new PartySongPlayer());
    }
}
