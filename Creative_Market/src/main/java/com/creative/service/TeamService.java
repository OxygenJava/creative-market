package com.creative.service;

import com.creative.domain.team;
import com.creative.dto.Result;

public interface TeamService {
    Result selectTeamAll(Integer id);
    Result updateTeam(team team);
}
