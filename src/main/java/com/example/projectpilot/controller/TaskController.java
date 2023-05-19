package com.example.projectpilot.controller;

import com.example.projectpilot.model.Task;
import com.example.projectpilot.model.User;
import com.example.projectpilot.repository.TaskRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Controller
public class TaskController
{
    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository)
    {
        this.taskRepository = taskRepository;
    }


    @GetMapping("/addTask")
    public String showAddTask(HttpSession session, Model model)
    {
        if ( session.getAttribute("user") == null )
        {
            return "redirect:/";
        }
        // Create a new Task object and add it to the model
        model.addAttribute("task", new Task());
        return "addTask";
    }

    // Poster ny task
    @PostMapping("/addTask")
    public String addTask(@RequestParam("task-title") String newTitle,
                          @RequestParam("task-description") String newDescription,
                          @RequestParam("task-note") String newNote,
                          @RequestParam("task-hours") int newHours,
                          @RequestParam("task-start_date") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate newStartDate,
                          @RequestParam("task-end_date") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate newEndDate,
                          @RequestParam("task-status") String newStatus,
                          @RequestParam("task-department") String newDepartment,
                          HttpSession session)
    {
        if ( session.getAttribute("user") == null )
        {
            return "redirect:/";
        }

        // Convert LocalDate to java.sql.Date
        Date sqlStartDate = Date.valueOf(newStartDate);
        Date sqlEndDate = Date.valueOf(newEndDate);

        // Laver en ny Task
        Task newTask = new Task();
        newTask.setTitle(newTitle);
        newTask.setDescription(newDescription);
        newTask.setNote(newNote);
        newTask.setHours(newHours);
        newTask.setStart_Date(sqlStartDate);
        newTask.setEnd_Date(sqlEndDate);
        newTask.setStatus(newStatus);
        newTask.setDepartment(newDepartment);

        // Gemmer i taskRepository
        taskRepository.addTask(newTask);

        // Går tilbage til alle tasks
        return "redirect:/allTasks";
    }

    // Viser update task siden
    @GetMapping("/updateTask/{id}")
    public String showUpdateTask(@PathVariable("id") int updateId, HttpSession session, Model model)
    {
        if ( session.getAttribute("user") == null )
        {
            return "redirect:/";
        }

        //find produkt med id=updateId i databasen
        Task updateTask = taskRepository.getTaskByTaskId(updateId);

        //tilføj produkt til view model, så det kan bruges i Thymeleaf
        model.addAttribute("task", updateTask);

        //fortæl Spring hvilken HTML-side, der skal vises
        return "updateTask";
    }

    // Poster update til eksisterende task (UDEN FLAG)
    @PostMapping("/updateTask")
    public String updateTask(@RequestParam("task-task_id") int updateTaskId,
                             @RequestParam("task-user_id") int updateUserId,
                             @RequestParam("task-title") String updateTitle,
                             @RequestParam("task-description") String updateDescription,
                             @RequestParam("task-note") String updateNote,
                             @RequestParam("task-hours") int updateHours,
                             @RequestParam("task-pay_rate") int updatePayRate,
                             @RequestParam("task-start_date") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate updateStartDate,
                             @RequestParam("task-end_date") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate updateEndDate,
                             @RequestParam("task-status") String updateStatus,
                             @RequestParam("task-department") String updateDepartment,
                             HttpSession session)
    {
        if ( session.getAttribute("user") == null )
        {
            return "redirect:/";
        }

        // Convert LocalDate to java.sql.Date
        Date sqlStartDate = Date.valueOf(updateStartDate);
        Date sqlEndDate = Date.valueOf(updateEndDate);


        //lav produkt ud fra parametre
        Task updateTask = new Task(updateTaskId, updateUserId, updateTitle, updateDescription, updateNote, updateHours, updatePayRate, sqlStartDate, sqlEndDate, updateStatus, updateDepartment);

        //kald opdater i repository
        taskRepository.updateTask(updateTask);

        //rediger til oversigtssiden
        return "redirect:/allTasks";
    }


    // Sletter en task
    @PostMapping("/deleteTask/{id}")
    public String deleteTask(@PathVariable("id") int taskId, HttpSession session)
    {
        if ( session.getAttribute("user") == null )
        {
            return "redirect:/";
        }
        // Slet task med given taskId fra taskRepository
        taskRepository.deleteTaskByID(taskId);

        // Går tilbage til alle tasks
        return "redirect:/allTasks";
    }

    @GetMapping("/userTasks")
    public String showUserTasks(HttpSession session, Model model)
    {
        if ( session.getAttribute("user") == null )
        {
            return "redirect:/";
        }

        User user = (User) session.getAttribute("user");
        List<Task> taskList = taskRepository.getAllTasksByUserID(user.getId());
        // Den her gør ikke noget, så den skal nok bare slettes tilsidsts
        //session.setAttribute("taskList", taskList);
        model.addAttribute("task", taskList);

        return "userTasks";
    }

    // Viser alle tasks
    @GetMapping("/allTasks")
    public String showAllTasks(HttpSession session, Model model)
    {
        if ( session.getAttribute("user") == null )
        {
            return "redirect:/";
        }
        model.addAttribute("task", taskRepository.getAllTasks());
        return "allTasks";
    }
}