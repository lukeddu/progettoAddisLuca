package it.uniroma3.siw.taskmanager.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import it.uniroma3.siw.taskmanager.controller.session.SessionData;
import it.uniroma3.siw.taskmanager.controller.validation.TaskValidator;
import it.uniroma3.siw.taskmanager.model.Project;
import it.uniroma3.siw.taskmanager.model.Task;
import it.uniroma3.siw.taskmanager.model.User;
import it.uniroma3.siw.taskmanager.service.ProjectService;
import it.uniroma3.siw.taskmanager.service.TaskService;


@Controller
public class TaskController {
   @Autowired
   TaskService taskService;
   
   @Autowired
   	SessionData sessionData;
   
   @Autowired
   ProjectService projectService;
   
   @Autowired
   TaskValidator taskValidator;
   
   @RequestMapping(value= {"/projects/{projectId}/addTask"}, method=RequestMethod.GET)
	public String addTaskForm(Model model, @PathVariable Long projectId) {
		User loggedUser=sessionData.getLoggedUser();
		model.addAttribute("user",loggedUser);
		model.addAttribute("currentProject", this.projectService.getProject(projectId));
		model.addAttribute("taskForm", new Task());
		return "createTask";
	}
	
	@RequestMapping(value= {"/projects/{projectId}/addTask"}, method=RequestMethod.POST)
	public String addTaskForm(@Valid @ModelAttribute("taskForm") Task task, 
			              BindingResult taskBindingResult, Model model, @PathVariable Long projectId) {
		
		User loggedUser=sessionData.getLoggedUser();
		this.taskValidator.validate(task, taskBindingResult);
		Project project=this.projectService.getProject(projectId);
		if(project!=null) {
		   if(!taskBindingResult.hasErrors()) {
			project.addTask(task);
			this.projectService.saveProject(project);
		    return "redirect:/projects/" + project.getId();
		}
		}
		   else return "redirect:/projects";
		   
		model.addAttribute("user", loggedUser);
		model.addAttribute("currentProject", this.projectService.getProject(projectId));
		return "createTask";
}
	@RequestMapping(value={"/projects/{projectId}/{taskId}/deleteTask"}, method=RequestMethod.POST)
	public String deleteTask(Model model, @PathVariable Long taskId, @PathVariable Long projectId) {
		Task task=this.taskService.getTask(taskId);
		this.taskService.deleteTask(task);
		return "redirect:/projects/" + projectId;
	}
	
	@RequestMapping(value= {"/projects/{projectId}/{taskId}/updateTask"}, method=RequestMethod.GET)
	public String updateTaskForm(Model model, @PathVariable Long projectId, @PathVariable Long taskId ) {
		User loggedUser=sessionData.getLoggedUser();
		model.addAttribute("user",loggedUser);
		model.addAttribute("currentProject", this.projectService.getProject(projectId));
		model.addAttribute("taskForm", this.taskService.getTask(taskId));
		return "updateTask";
	}
	
	@RequestMapping(value= {"/projects/{projectId}/{taskId}/updateTask"}, method=RequestMethod.POST)
	public String updateTaskForm(@Valid @ModelAttribute("taskForm") Task updateTask, 
			              BindingResult taskBindingResult, Model model, @PathVariable Long projectId, @PathVariable Long taskId) {
		
		User loggedUser=sessionData.getLoggedUser();
		taskValidator.validate(updateTask, taskBindingResult);
		Task task=this.taskService.getTask(taskId);
		Project project=this.projectService.getProject(projectId);
		
		if(project!=null) {
		if(!taskBindingResult.hasErrors()) {
			task.setDescription(updateTask.getDescription());
			task.setName(updateTask.getName());
			this.taskService.saveTask(task);
		    return "redirect:/projects/" + projectId;
		}
		}
		else return"redirect:/projects";
		
		model.addAttribute("user", loggedUser);
		model.addAttribute("currentProject", project);
		model.addAttribute("taskForm", task);
		return "updateTask";
	}
}
