/*
 * AuthenticatedPatronUpdateService.java
 *
 * Copyright (c) 2019 Rafael Corchuelo.
 *
 * In keeping with the traditional purpose of furthering education and research, it is
 * the policy of the copyright owner to permit non-commercial use and redistribution of
 * this software. It has been tested carefully, but it is not guaranteed for any particular
 * purposes. The copyright owner does not offer any warranties or representations, nor do
 * they accept any liabilities with respect to them.
 */

package acme.features.authenticated.patron;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.roles.Patron;
import acme.framework.components.Errors;
import acme.framework.components.HttpMethod;
import acme.framework.components.Model;
import acme.framework.components.Request;
import acme.framework.components.Response;
import acme.framework.entities.Authenticated;
import acme.framework.entities.Principal;
import acme.framework.helpers.PrincipalHelper;
import acme.framework.services.AbstractUpdateService;

@Service
public class AuthenticatedPatronUpdateService implements AbstractUpdateService<Authenticated, Patron> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AuthenticatedPatronRepository repository;


	// AbstractUpdateService<Authenticated, Patron> interface -----------------

	@Override
	public boolean authorise(final Request<Patron> request) {
		assert request != null;

		Patron result;
		Principal principal;
		int userAccountId;

		principal = request.getPrincipal();
		userAccountId = principal.getAccountId();

		result = this.repository.findOnePatronByUserAccountId(userAccountId);

		return result != null;
	}

	@Override
	public void validate(final Request<Patron> request, final Patron entity, final Errors errors) {
		assert request != null;
		assert entity != null;
		assert errors != null;
	}

	@Override
	public void bind(final Request<Patron> request, final Patron entity, final Errors errors) {
		assert request != null;
		assert entity != null;
		assert errors != null;

		request.bind(entity, errors);
	}

	@Override
	public void unbind(final Request<Patron> request, final Patron entity, final Model model) {
		assert request != null;
		assert entity != null;
		assert model != null;

		if (entity.getCard() != null) {
			String showCard = "/patron/card/show" + "?id=" + entity.getCard().getId();
			model.setAttribute("showCard", showCard);
			model.setAttribute("hasCard", true);
		} else {
			String associateCard = "/patron/card/associate" + "?id=" + entity.getId();
			model.setAttribute("associateCard", associateCard);
			model.setAttribute("hasCard", false);
		}

		request.unbind(entity, model, "organisation", "card");
	}

	@Override
	public Patron findOne(final Request<Patron> request) {
		assert request != null;

		Patron result;
		Principal principal;
		int userAccountId;

		principal = request.getPrincipal();
		userAccountId = principal.getAccountId();

		result = this.repository.findOnePatronByUserAccountId(userAccountId);

		return result;
	}

	@Override
	public void update(final Request<Patron> request, final Patron entity) {
		assert request != null;
		assert entity != null;

		this.repository.save(entity);
	}

	@Override
	public void onSuccess(final Request<Patron> request, final Response<Patron> response) {
		assert request != null;
		assert response != null;

		if (request.isMethod(HttpMethod.POST)) {
			PrincipalHelper.handleUpdate();
		}
	}

}
