package it.uniroma3.siw.taskmanager.controller.validation;

import it.uniroma3.siw.taskmanager.model.Task;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component
public class TaskValidator implements Validator{
	
	final Integer MAX_NAME_LENGTH=20;
	final Integer MIN_NAME_LENGTH=2;
	final Integer MAX_DESCRIPTION_LENGTH=1000;

	@Override
	public void validate(Object o, Errors errors) {
        Task task = (Task) o;
        String name = task.getName().trim();
        String description = task.getDescription().trim();

        if (name.trim().isEmpty())
            errors.rejectValue("name", "required");
        else if (name.length() < MIN_NAME_LENGTH || name.length() > MAX_NAME_LENGTH)
            errors.rejectValue("name", "size");
        
        if (description.length() > MAX_DESCRIPTION_LENGTH)
            errors.rejectValue("description", "size");
    }
	
	
    @Override
    public boolean supports(Class<?> clazz) {
        return Task.class.equals(clazz);
    }

}
