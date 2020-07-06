package com.google.sps.data;

public final class Comment {

  private final String partyId;
  private final String text;
  private final String name;
  private final long timestamp;

  public Comment(String partyId, String text, String name, long timestamp) {
    this.partyId = partyId;
    this.text = text;
    this.name = name;
    this.timestamp = timestamp;
  }
}