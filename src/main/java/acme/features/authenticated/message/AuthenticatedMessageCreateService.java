/*
 * AuthenticatedMessageCreateService.java
 *
 * Copyright (c) 2019 Rafael Corchuelo.
 *
 * In keeping with the traditional purpose of furthering education and research, it is
 * the policy of the copyright owner to permit non-commercial use and redistribution of
 * this software. It has been tested carefully, but it is not guaranteed for any particular
 * purposes. The copyright owner does not offer any warranties or representations, nor do
 * they accept any liabilities with respect to them.
 */

package acme.features.authenticated.message;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.forums.Forum;
import acme.entities.messages.Message;
import acme.features.authenticated.forum.AuthenticatedForumRepository;
import acme.framework.components.Errors;
import acme.framework.components.Model;
import acme.framework.components.Request;
import acme.framework.entities.Authenticated;
import acme.framework.entities.Principal;
import acme.framework.services.AbstractCreateService;

@Service
public class AuthenticatedMessageCreateService implements AbstractCreateService<Authenticated, Message> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AuthenticatedMessageRepository	repository;

	@Autowired
	private AuthenticatedForumRepository	forumRepository;
	// AbstractCreateService<Authenticated, Message> ---------------------------


	@Override
	public boolean authorise(final Request<Message> request) {
		assert request != null;

		Integer forumId = request.getModel().getInteger("id");

		if (forumId != null) {
			Principal principal = request.getPrincipal();
			boolean res = this.forumRepository.findManyForumsByUserId(principal.getAccountId()).stream().anyMatch(f -> f.getId() == forumId.intValue());
			return res;
		}

		return true;
	}

	@Override
	public void validate(final Request<Message> request, final Message entity, final Errors errors) {
		assert request != null;
		assert entity != null;
		assert errors != null;

	}

	@Override
	public void bind(final Request<Message> request, final Message entity, final Errors errors) {
		assert request != null;
		assert entity != null;
		assert errors != null;

		request.bind(entity, errors);
	}

	@Override
	public void unbind(final Request<Message> request, final Message entity, final Model model) {
		assert request != null;
		assert entity != null;
		assert model != null;

		int idForum = request.getModel().getInteger("id");
		String createMessage = "../message/create?id=" + idForum;
		model.setAttribute("direccionForum", createMessage);

		request.unbind(entity, model, "title", "moment", "tags", "body", "user", "forum");
	}

	@Override
	public Message instantiate(final Request<Message> request) {
		assert request != null;

		Date moment;
		moment = new Date(System.currentTimeMillis() - 1);

		Principal principal = request.getPrincipal();
		int authenticatedId = principal.getAccountId();
		Authenticated authenticated = this.repository.findOneAuthenticatedByUserAccountId(authenticatedId);

		int idthread = request.getModel().getInteger("id");
		Forum forum = this.repository.findForumById(idthread);

		Message result;
		result = new Message();
		result.setMoment(moment);
		result.setUser(authenticated);
		result.setForum(forum);

		return result;
	}

	@Override
	public void create(final Request<Message> request, final Message entity) {
		assert request != null;
		assert entity != null;

		this.repository.save(entity);
	}

}
