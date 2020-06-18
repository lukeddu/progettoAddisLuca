package it.uniroma3.siw.taskmanager.controller;

import java.util.List;

import javax.validation.Valid;

import it.uniroma3.siw.taskmanager.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.taskmanager.controller.session.SessionData;
import it.uniroma3.siw.taskmanager.controller.validation.ProjectValidator;
import it.uniroma3.siw.taskmanager.repository.ProjectRepository;
import it.uniroma3.siw.taskmanager.service.CredentialsService;
import it.uniroma3.siw.taskmanager.service.ProjectService;
import it.uniroma3.siw.taskmanager.model.*;


@Controller
public class ProjectController {
	@Autowired
	ProjectRepository projectRepository;
	
	@Autowired
	ProjectService projectService;
	
	@Autowired
	CredentialsService credentialsServiceService;
	
	@Autowired
     ProjectValidator projectValidator;

	@Autowired
	SessionData sessionData;
	
	@RequestMapping(value= {"/projects"}, method=RequestMethod.GET)
	public String myOwnerProject(Model model) {
		User loggedUser= sessionData.getLoggedUser();
		List<Project> projects=projectService.retriveProjectsOwnedBy(loggedUser);
		model.addAttribute("loggedUser", loggedUser);
		model.addAttribute("projects", projects);
		return "myOwnedProjects";
	}
	
	@RequestMapping(value= {"/projects/{projectId}"}, method=RequestMethod.GET)
	public String project(Model model, @PathVariable Long projectId) {
		Project project=projectService.getProject(projectId);
		if(project==null)
			return "redirect:/projects";
		User loggedUser= sessionData.getLoggedUser();
		List<User> members = project.getMembers();
		if(!project.getOwner().equals(loggedUser) && !members.contains(loggedUser))
			return "redirect:/projects";
		
		model.addAttribute("loggedUser", loggedUser);
		model.addAttribute("project", project);
		model.addAttribute("members", members);
		return "project";
	}
	
	@RequestMapping(value={"/projects/{projectId}/delete"}, method=RequestMethod.POST)
	public String deleteProject(Model model, @PathVariable Long projectId) {
		Project project=this.projectService.getProject(projectId);
		this.projectService.deleteProject(project);
		return "redirect:/projects";
	}
	
	@RequestMapping(value= {"/projectsShared"}, method=RequestMethod.GET)
	public String sharedProjects(Model model) {
		User loggedUser= sessionData.getLoggedUser();
		List<Project> projects=projectService.retriveProjectsSharedWith(loggedUser);
		model.addAttribute("loggedUser", loggedUser);
		model.addAttribute("projects", projects);
		return "sharedProjects";
	}
	
	@RequestMapping(value= {"/projects/add"}, method=RequestMethod.GET)
	public String createProjectForm(Model model) {
		User loggedUser=sessionData.getLoggedUser();
		model.addAttribute("user", loggedUser);
		model.addAttribute("projectForm", new Project());
		return "addProject";
	}
	
	@RequestMapping(value= {"/projects/add"}, method=RequestMethod.POST)
	public String createProjectForm(@Valid @ModelAttribute("projectForm") Project project,
			              BindingResult projectBindingResult, Model model) {
		
		User loggedUser=sessionData.getLoggedUser();
		projectValidator.validate(project, projectBindingResult);
		if(!projectBindingResult.hasErrors()) {
			project.setOwner(loggedUser);
			this.projectService.saveProject(project);
		return "redirect:/projects/" + project.getId();
		}
		model.addAttribute("user", loggedUser);
		return "addProject";
	}
	
	@RequestMapping(value= {"/projects/{projectId}/update"}, method=RequestMethod.GET)
	public String updateProjectForm(Model model, @PathVariable Long projectId) {
		User loggedUser=sessionData.getLoggedUser();
		model.addAttribute("user",loggedUser);
		model.addAttribute("projectForm", this.projectService.getProject(projectId));
		return "updateProject";
	}
	
	@RequestMapping(value= {"/projects/{projectId}/update"}, method=RequestMethod.POST)
	public String updateProjectForm(@Valid @ModelAttribute("projectForm") Project updateProject, 
			              BindingResult projectBindingResult, Model model, @PathVariable Long projectId) {
		
		User loggedUser=sessionData.getLoggedUser();
		projectValidator.validate(updateProject, projectBindingResult);
		Project project=this.projectService.getProject(projectId);
		
		if(project!=null) {
		if(!projectBindingResult.hasErrors()) {
			project.setDescription(updateProject.getDescription());
			project.setName(updateProject.getName());
			
			this.projectService.saveProject(project);
		return "redirect:/projects/" + project.getId();
		}
		}
		else return "redirect:/projects";
		
		model.addAttribute("user", loggedUser);
		model.addAttribute("projectForm", project);
		return "updateProject";
	}
	
	@RequestMapping(value={"/projects/{projectId}/share"}, method=RequestMethod.POST)
	public String shareProject(@RequestParam("username") String username, Model model, @PathVariable Long projectId) {
		Credentials credentials=this.credentialsServiceService.getCredentials(username);
		Project project=this.projectService.getProject(projectId);
		
		if(credentials!=null) {
		projectService.shareProjectWithUser(project, credentials.getUser());
		return "redirect:/projects/" +projectId;
		}
		return "redirect:/projects";
	}
	
}
