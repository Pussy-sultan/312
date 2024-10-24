package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.*;

@RequestMapping(value = "/admin")
@Controller
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public String adminPage(Model model) {
        List<User> userList = userService.getAll();
        Map<Integer, String> mapa = new HashMap<>();
        for (User user : userList) {
            mapa.put(user.getId(), userService.getRoleListByUser(user));
        }

        model.addAttribute("users", userList);
        model.addAttribute("roles", mapa);
        return "users";
    }

    @GetMapping(value = "/user")
    public String formUserPage(Model model, @RequestParam(value = "id", defaultValue = "0") int id) {
        User user = new User();
        Set<Integer> roleIdList = new HashSet<>();
        if (id != 0) {
            user = userService.getById(id);
            for (Role role: user.getRoles()) {
                roleIdList.add(role.getId());
            }
        }

        model.addAttribute("allRoles", roleService.findAll());
        model.addAttribute("selectedRoleList", roleIdList);
        model.addAttribute("admin", user.getEmail());
        model.addAttribute("user", user);
        return "form";
    }

    @GetMapping(value = "/delete/")
    public String deleteUser(@RequestParam(value = "id") int id) {
        userService.deleteById(id);
        return "redirect:/admin";
    }

    @PostMapping
    public String saveUser(@ModelAttribute("user") User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "form";
        }
        userService.save(user);
        return "redirect:/admin";

    }
}
