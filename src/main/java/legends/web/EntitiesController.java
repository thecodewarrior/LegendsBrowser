package legends.web;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import legends.helper.EventHelper;
import legends.helper.Templates;
import legends.model.Entity;
import legends.model.World;
import legends.web.basic.Controller;
import legends.web.basic.RequestMapping;

@Controller
public class EntitiesController {

	@RequestMapping("")
	public Template index(VelocityContext context) {
		context.put("entityMap", World.getMainCivilizations().stream().collect(Collectors.groupingBy(Entity::getRace)));

		return Templates.get("index.vm");
	}

	@RequestMapping("/chord.json")
	public Template chordData(VelocityContext context) {
		context.put("entities", World.getMainCivilizations().stream().filter(e->e.getWarEnemies().size()>0).sorted((e1, e2) -> {
			return e1.getRace().compareTo(e2.getRace());
		}).collect(Collectors.toList()));

		return Templates.get("chord.vm");
	}
	
	@RequestMapping("/entities")
	public Template structures(VelocityContext context) {
		Map<String,List<Entity>> entities = World.getEntities().stream().filter(e -> !e.getName().equals("UNKNOWN")).collect(Collectors.groupingBy(Entity::getType));
		context.put("entities", entities);
		List<String> types = entities.keySet().stream().sorted((t1,t2) -> (entities.get(t1).size() < entities.get(t2).size()) ? 1 : -1).collect(Collectors.toList());
		context.put("types", types);
		return Templates.get("entities.vm");
	}

	@RequestMapping("/entity/{id}")
	public Template entity(VelocityContext context, int id) {
		Entity en = World.getEntity(id);

		context.put("entity", en);
		context.put("events", World.getHistoricalEvents().stream().filter(e -> EventHelper.related(en, e))
				.collect(Collectors.toList()));

		return Templates.get("entity.vm");
	}
	
	@RequestMapping("/set/racecolor")
	public Template setRaceColor(VelocityContext context) {
		Entity.setRaceColor(context.get("race").toString(), context.get("color").toString());
		
		return Templates.get("empty.vm");
	}
}
