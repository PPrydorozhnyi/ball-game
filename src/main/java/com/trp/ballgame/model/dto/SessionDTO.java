package com.trp.ballgame.model.dto;

import java.util.List;

public record SessionDTO(List<String> players, int estimated, String password) {
}
