
package acme.features.entrepreneur.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.activities.Activity;
import acme.entities.roles.Entrepreneur;
import acme.entities.rounds.Round;
import acme.features.entrepreneur.round.EntrepreneurRoundRepository;
import acme.framework.components.Errors;
import acme.framework.components.Model;
import acme.framework.components.Request;
import acme.framework.entities.Principal;
import acme.framework.services.AbstractCreateService;

@Service
public class EntrepreneurActivityCreateService implements AbstractCreateService<Entrepreneur, Activity> {

	@Autowired
	EntrepreneurActivityRepository	repository;
	@Autowired
	EntrepreneurRoundRepository		roundRepository;


	@Override
	public boolean authorise(final Request<Activity> request) {
		assert request != null;

		boolean result;
		int roundId;
		Round round;
		Entrepreneur entrepreneur;
		Principal principal;

		roundId = request.getModel().getInteger("id");
		round = this.repository.findOneRoundById(roundId);
		entrepreneur = round.getEntrepreneur();
		principal = request.getPrincipal();
		result = entrepreneur.getUserAccount().getId() == principal.getAccountId();

		return result;
	}

	@Override
	public void bind(final Request<Activity> request, final Activity entity, final Errors errors) {
		assert request != null;
		assert entity != null;
		assert errors != null;

		request.bind(entity, errors);
	}

	@Override
	public void unbind(final Request<Activity> request, final Activity entity, final Model model) {
		assert request != null;
		assert entity != null;
		assert model != null;

		String direccionActivity = "../activity/create?id=" + request.getModel().getInteger("id");
		model.setAttribute("direccionActivity", direccionActivity);

		request.unbind(entity, model, "title", "start", "end", "budget");
	}

	@Override
	public Activity instantiate(final Request<Activity> request) {
		assert request != null;

		Activity result;

		result = new Activity();
		result.setRound(this.repository.findOneRoundById(request.getModel().getInteger("id")));
		return result;
	}

	@Override
	public void validate(final Request<Activity> request, final Activity entity, final Errors errors) {
		assert request != null;
		assert entity != null;
		assert errors != null;
		if (entity.getStart() != null && entity.getEnd() != null) {
			Date date = new Date();
			errors.state(request, entity.getStart().compareTo(date) > 0, "start", "entrepreneur.activity.startDateMustBeFuture");
			errors.state(request, entity.getEnd().compareTo(entity.getStart()) > 0, "end", "entrepreneur.activity.endDateCantBeSoonerThanStart");
		}
		if (entity.getBudget() != null) {
			Integer roundId = request.getModel().getInteger("id");
			Round round = this.repository.findOneRoundById(roundId);
			List<Activity> actividades = new ArrayList<>(this.repository.findManyByRound(roundId));
			actividades.add(entity);
			double suma = actividades.stream().mapToDouble(m -> m.getBudget().getAmount()).sum();
			errors.state(request, suma <= round.getMoney().getAmount(), "budget", "entrepreneur.activity.moneyQuantityExceeded");
		}
	}

	@Override
	public void create(final Request<Activity> request, final Activity entity) {
		assert request != null;
		assert entity != null;

		entity.setRound(this.repository.findOneRoundById(request.getModel().getInteger("id")));

		this.repository.save(entity);
	}

}
