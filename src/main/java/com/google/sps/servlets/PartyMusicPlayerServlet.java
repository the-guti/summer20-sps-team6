package com.google.sps.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.sps.data.PartyPlaylistState;
import com.google.sps.data.YoutubeSongPlayInfo;
import com.google.sps.media.PartySongPlayer;
import com.google.sps.media.YoutubeSong;
import com.google.sps.utils.Parties;
import com.google.sps.utils.Requests;

/**
 * Uses UserService and DataStore to allow Creation, Reading, and Updating of User objects
 * JSON is sent on GET, on POST create a new user object or update individual parameters
 * Because all Party players are stored locally, if no player exists for a given room on a request, a default player is created
 */
@WebServlet("/musicPlayer")
public class PartyMusicPlayerServlet extends HttpServlet {
    private Map<String, PartySongPlayer> partySongPlayers = new HashMap<>();
    private static Gson gson = new Gson();

    private enum Action {
        START_PLAYER,
        STOP_PLAYER,
        SKIP_SONG,
        ADD_SONG,
        SEEK_TIME,
    }

    /**
     * Sends JSON of the current party-id's YoutubeSongPlayInfo
     * If no room-id is given or a non-numeric room-id is given, return a 400 response code
     * If there is no song playing, the response code will be 200, but there will be no message body
     * If room-id isn't a valid room
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long partyId;
        try {
            partyId = Long.parseLong(Requests.getParameter(request, "party-id", null));
        } catch (Exception e) {
            response.setStatus(400);
            return;
        }
        if (!Parties.isPartyCreated(partyId)) {
            response.setStatus(400);
            return;
        } else if (!Parties.isPartyPlayerCreated(partyId)) {
            Parties.createOrReplacePartySongPlayer(partyId);
        }
        YoutubeSongPlayInfo currentSongInfo = Parties.getPartySongPlayer(partyId).getCurrentSongInformation();
        List<YoutubeSong> currentPlaylist = Parties.getPartySongPlayer(partyId).getCurrentPlaylist();
        PartyPlaylistState currentPlayerInfo = new PartyPlaylistState(currentSongInfo, currentPlaylist);
        if (currentSongInfo == null){
            // response.setStatus(204);
            response.setContentType("application/json;"); 
            response.getWriter().println("{\"currentSongPlayInfo\":{\"song\":{\"songName\":\"J. Balvin - Rojo\",\"videoId\":\"5drPJOatAQg\",\"songDuration\":60000},\"songStartGmtTimeMs\":0,\"stopped\":true},\"currentPlaylist\":[{\"songName\":\"Queen - We Are The Champions\",\"videoId\":\"KXw8CRapg7k\",\"songDuration\":60000},{\"songName\":\"Queen - Radio Ga Ga\",\"videoId\":\"azdwsXLmrHE\",\"songDuration\":60000}]}"); 
            /*"*/
            return;
        } else {
            
            String songPlaylistStatusJson = gson.toJson(currentPlayerInfo);
            response.setContentType("application/json;");
            response.getWriter().println(songPlaylistStatusJson);
        }
    }

    /**
     * Required Parameters: action, party-id
     * Actions:
     * START_PLAYER: starts the party's player
     * STOP_PLAYER: stops the party's player
     * ADD_SONG: Needs parameter "youtube-song-json", adds that song to party player's queue
     * SKIP_SONG: skips the party player's current song
     * SEEK_TIME: Needs parameter "seek-time", seeks to that time in the current song
     * <p>
     * If a needed or required parameter is missing return a 400 response code
     * If party-id refers to a non-existent party return a 404 response code
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long partyId;
        Action action;
        System.out.println("doPost");
        try {
            partyId = Long.parseLong(Requests.getParameter(request, "party-id", null));
            action = Action.valueOf(Requests.getParameter(request, "action", ""));
        } catch (Exception e) {
            response.setStatus(400);
            return;
        }
        if (!Parties.isPartyCreated(partyId)) {
            response.setStatus(404);
            return;
        }
        if (!Parties.isPartyPlayerCreated(partyId)) {
            Parties.createOrReplacePartySongPlayer(partyId);
        }
        PartySongPlayer currentPartyPlayer = Parties.getPartySongPlayer(partyId);
        switch (action) {
            case START_PLAYER:
                currentPartyPlayer.startPlayer();
                break;
            case STOP_PLAYER:
                currentPartyPlayer.stopPlayer();
                break;
            case ADD_SONG:
                if (!Requests.hasParameterValue(request, "youtube-song-json")) {
                    response.setStatus(400);
                    System.out.println("missing json param");
                    return;
                }
                try {
                    YoutubeSong youtubeSong = gson.fromJson(request.getParameter("youtube-song-json"), YoutubeSong.class);
                    currentPartyPlayer.addSong(youtubeSong);
                } catch (Exception e) {
                    System.out.println("ugly json");
                    response.setStatus(400);
                    return;
                }
                break;
            case SKIP_SONG:
                currentPartyPlayer.playNextSong();
                break;
            case SEEK_TIME:
                try {
                    long seekTimeInSong = Long.parseLong(Requests.getParameter(request, "seek-time", null));
                    currentPartyPlayer.seek(seekTimeInSong);
                } catch (Exception e) {
                    response.setStatus(400);
                    return;
                }
        }
        response.sendRedirect("/party.html?id=" + partyId);
    }
}
