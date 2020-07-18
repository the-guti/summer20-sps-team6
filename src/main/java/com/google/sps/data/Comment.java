package com.google.sps.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public final class Comment {
  private final String partyId;
  private final String text;
  private final String name;
  private final long timestamp;
}