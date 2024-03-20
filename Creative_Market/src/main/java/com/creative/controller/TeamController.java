package com.creative.controller;

import com.creative.domain.team;
import com.creative.dto.Result;
import com.creative.service.impl.CrowServiceImpl;
import com.creative.service.impl.TeamServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/crow/team")
@CrossOrigin
public class TeamController {
    @Autowired
    private TeamServiceImpl teamService;

    @GetMapping("/{id}")
    public Result selectTeamAll(@PathVariable Integer id){
        Result result = teamService.selectTeamAll(id);
        return result;
    }

    @GetMapping
    public Result selectTeam(){
        Result result = teamService.selectTeam();
        return result;
    }

    @PutMapping("/update")
    public Result updateTeam(@RequestBody team team){
        Result result = teamService.updateTeam(team);
        return result;
    }

    @DeleteMapping("/{id}")
    public Result deleteTeam(@PathVariable Integer id){
        Result result = teamService.deleteTeam(id);
        return result;
    }

    @PostMapping
    public Result insertTeam(){
        Result result = teamService.insertTeam();
        return result;
    }

    @PutMapping("update/user")
    public Result insertTeamUser(@RequestBody team team){
        Result result = teamService.insertTeamUser(team);
        return result;
    }
}
