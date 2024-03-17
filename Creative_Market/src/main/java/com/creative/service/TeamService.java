package com.creative.service;

import com.creative.domain.team;
import com.creative.dto.Result;

public interface TeamService {
    Result selectTeamAll(Integer id);
    Result updateTeam(team team);
    Result insertTeam(team team);
    Result deleteTeam(Integer id);
    Result selectTeam();
    Result insertTeamUser(team team);
}
