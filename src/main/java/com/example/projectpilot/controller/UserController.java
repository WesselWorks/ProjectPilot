package com.example.projectpilot.controller;

import com.example.projectpilot.model.Task;
import com.example.projectpilot.model.User;
import com.example.projectpilot.repository.TaskRepository;
import com.example.projectpilot.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController
{

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    @Autowired
    public UserController(UserRepository userRepository, TaskRepository taskRepository)
    {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    @GetMapping("/")
    public String showStart()
    {
        return "start";
    }

    // This method is used to show the login page.
    @GetMapping("/login")
    public String showLogin()
    {
        return "login";
        //Ingen HttpSession her da man her logger ind.
    }

    // This method is used to verify the user.
    @PostMapping("/login")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        Model model,
                        HttpSession session)
    {
        //Check if user with mail already exists
        if ( !userRepository.verifyUser(email, password) )
        {
            model.addAttribute("errorMessage", "Email or password invalid");
            return "login";
        }
        else
        {
            User user = userRepository.getUserByEmailAndPassword(email, password);
            session.setAttribute("user", user);
            return "redirect:/allTasks";
        }
    }

    // This method is used to log out the user and invalidate the session.
    @GetMapping("/logout")
    public String logout(HttpSession session)
    {
        session.invalidate();
        return "redirect:/";
    }

    // This method is used to show the register page.
    @GetMapping("/register")
    public String registerUser()
    {
        return "register";
        //Ingen HTTPSession her fordi alle skal kunne lave et login.
    }

    // This method is used to register the user.
    @PostMapping("/register")
    public String registerUser(@RequestParam("admin") boolean admin,
                               @RequestParam("first_name") String firstname,
                               @RequestParam("last_name") String lastname,
                               @RequestParam("email") String email,
                               @RequestParam("password") String password,
                               Model model,
                               HttpSession session)
    {
        //Check if user with mail already exists
        if ( !userRepository.checkIfUserExists(email) )
        {
            User user = new User(admin, firstname, lastname, email, password);
            userRepository.addUser(user);
            session.setAttribute("user", user);
            return "redirect:/allTasks";
        }
        else
        {
            model.addAttribute("errorMessage", "Email already in use");
            return "register";
        }
    }

    //Gør udmiddelbart ikke noget, bare ligesom registerUser
    /*
    // This method is used to show all users.
    @GetMapping("/addUser")
    public String showAddUser(HttpSession session)
    {
        if ( session.getAttribute("user") == null )
        {
            return "redirect:/";
        }

        return "addUser";
    }


    // This method is used to add a user.
    @PostMapping("/addUser")
    public String addUser(@RequestParam("user-firstname") String newFirstName,
                          @RequestParam("user-lastname") String newLastName,
                          @RequestParam("user-email") String newEmail,
                          @RequestParam("user-password") String newPassword,
                          HttpSession session)
    {
        if ( session.getAttribute("user") == null )
        {
            return "redirect:/";
        }

        //lave en ny User
        User newUser = new User();
        newUser.setFirstName(newFirstName);
        newUser.setLastName(newLastName);
        newUser.setEmail(newEmail);
        newUser.setPassword(newPassword);

        //Gem ny User
        userRepository.addUser(newUser);

        //Tilbage til start så man kan logge ind
        return "start";
    }
    */


    // This method is used to show the assignUser page.
    @GetMapping("/assignUser/{id}")
    public String showAssignUser(@PathVariable("id") int task_Id, HttpSession session, Model model)
    {
        if ( session.getAttribute("user") == null )
        {
            return "redirect:/";
        }
        Task task = taskRepository.getTaskByTaskId(task_Id);

        // Set the task object as a model attribute
        model.addAttribute("task", task);
        model.addAttribute("users", userRepository.getAllUsers());
        return "assignUser";
    }

    // This method is used to assign a user to a task.
    @PostMapping("/assignUser")
    public String assignUser(@RequestParam("task_id") int task_id, @RequestParam("userId") int userId, HttpSession session)
    {
        if ( session.getAttribute("user") == null )
        {
            return "redirect:/";
        }
        Task updateTask = taskRepository.getTaskByTaskId(task_id);

        taskRepository.assignTo(updateTask, userId);

        return "redirect:/allTasks";
    }

    // This method is used to show all users page with alle users.
    @GetMapping("/allUsers")
    public String showAllUsers(HttpSession session, Model model)
    {
        if ( session.getAttribute("user") == null )
        {
            return "redirect:/";
        }
        model.addAttribute("user", userRepository.getAllUsers());
        return "allUsers";
    }

    // This method is used to show the admin page.
    @GetMapping("/admin")
    public String admin(HttpSession session)
    {
        User user = (User) session.getAttribute("user");
        if ( user == null || user.getId() != 1 )
        {
            return "redirect:/";
        }

        return "admin";
    }

    @PostMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable("id") int id, HttpSession session)
    {
        if ( session.getAttribute("user") == null )
        {
            return "redirect:/";
        }

        User user = userRepository.getUserByID(id);

        userRepository.deleteUserByID(user);
        return "redirect:/allUsers";
    }

    @GetMapping("/editUser/{id}")
    public String showEditUser(@PathVariable("id") int id, HttpSession session, Model model)
    {
        if ( session.getAttribute("user") == null )
        {
            return "redirect:/";
        }

        User user = userRepository.getUserByID(id);

        model.addAttribute("user", user);
        return "editUser";
    }

    @PostMapping("/editUser")
    public String editUser(@RequestParam("user-id") int id,
                           @RequestParam("admin") boolean admin,
                           @RequestParam("user-firstname") String firstname,
                           @RequestParam("user-lastname") String lastname,
                           @RequestParam("user-email") String email,
                           @RequestParam("user-password") String password,
                           HttpSession session)
    {
        if ( session.getAttribute("user") == null )
        {
            return "redirect:/";
        }

        User user = new User(id, admin, firstname, lastname, email, password);

        userRepository.updateUser(user);

        return "redirect:/allUsers";
    }

}
