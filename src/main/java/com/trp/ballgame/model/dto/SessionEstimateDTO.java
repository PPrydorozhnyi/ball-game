package com.trp.ballgame.model.dto;

import java.util.UUID;

public record SessionEstimateDTO(UUID sessionId, String password, int estimated) {
}
