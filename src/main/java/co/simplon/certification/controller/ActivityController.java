package co.simplon.certification.controller;

import co.simplon.certification.model.Activity;
import co.simplon.certification.model.Place;
import co.simplon.certification.repository.ActivityRepository;
import co.simplon.certification.repository.PlaceRepository;

import java.util.List;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin("http://localhost:4200")

@RestController
@RequestMapping("/api/activity")

public class ActivityController 
{
    //permet d'injecter DisciplineRepository dans mon contrôleur
    @Autowired
    // Créer une instance nommée disciplineRepo de DisciplineRepository
    private ActivityRepository activityRepo;
    
    @Autowired
    // Créer une instance nommée disciplineRepo de DisciplineRepository
    private PlaceRepository placeRepo;
    
    // Lister toutes les disciplines
    @GetMapping
    // Méthode GetAllAcivity() 
    // pour toutes les instances Activity présentes dans notre Repository
    // @return List<Activity> via activity(Repo.findAll()
    List<Activity> getAllActivity() 
    {
    	return activityRepo.findAll();
    }
    
    // Lister une discipline par l'id
    @GetMapping("/{id}")
    ResponseEntity<Activity> getActivityById(@PathVariable(value = "id") long id)
    {
        Activity activity = activityRepo.getOne(id);

        if (activity == null) 
        {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().body(activity);
    }

    @PostMapping 
    // @RequestBody dans mon URL je dois récupèrer un objet 
    // de type Json que je parse en type de mon argument de méthode
    // qui est pour ce cas un de mes modèles.
    // @Valid = validation au @RequestBody 
    Activity addActivity(@Valid @RequestBody Activity activity) 
    {
        return activityRepo.save(activity);
    }
    
    /* ajouter une activité en la liant a un lieu
      ===============================================
      Ajouter une activité suivante avec Postman :
      test de ma méthode addActivity
      POST : localhost:8080/api/activity/activity/place/1
      ===============================================*/
    @PostMapping("/activity/place/{id}")
    Activity postActivityByIdPlace(@PathVariable(value = "id") long id,
    		@Valid @RequestBody Activity activity) 
    {
    	// Après injection du repository de Place
    	// pour disposer des dépendances Jpa de Place
    	// je récupère mon place_id et ses attributs
    	Place place = placeRepo.getOne(id);
    	
    	// Je lie les entités Activity et place 
    	// grave aux relations OneToMany et ManyToOne 
    	// dans leur modèles des deux objets 
    	activity.setPlace(place);
    	
    	return activityRepo.save(activity);
    }
    
    // Modifier une discipline par l'id
    @PutMapping("/{id}")
    ResponseEntity<Activity> updateActivity(@PathVariable(value = "id") long id, 
    										@Valid @RequestBody Activity activity) 
    {
        Activity activityToUpdate = activityRepo.getOne(id);

        // Si l'occurence est null alors id non trouvé 
        if (activityToUpdate == null) 
        {
            return ResponseEntity.notFound().build();
        }

        // mise a jour de l'attribut discipline
        if (activity.getActivity() != null) 
        {
            activityToUpdate.setActivity(activity.getActivity());
        }

        // mise a jour de l'attribut horaire de début
        if (activity.getStartTime() != 0) 
        {
            activityToUpdate.setStartTime(activity.getStartTime());
        }
        
        // mise a jour de l'attribut horaire de fin
        if (activity.getEndTime() != 0) 
        {
            activityToUpdate.setEndTime(activity.getEndTime());
        }
        
        // retourne l'objet Activity avec tous ses attributs
        Activity updateActivity = activityRepo.save(activityToUpdate);

        return ResponseEntity.ok(updateActivity);
    }
    
    // Supprimer une discipline par l'id
    @DeleteMapping("/{id}")
    ResponseEntity<Activity> deleteActivity(@PathVariable(value = "id") long id) 
    {
        Activity activity = activityRepo.getOne(id);

        if (activity == null) 
        {
            return ResponseEntity.notFound().build();
        }
        
        // Suppression d'une discipline par l'id
        activityRepo.delete(activity);

        return ResponseEntity.ok().build();
    }
}