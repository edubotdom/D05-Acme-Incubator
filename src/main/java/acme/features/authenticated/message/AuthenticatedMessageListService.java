
package acme.features.authenticated.message;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.messages.Message;
import acme.features.authenticated.forum.AuthenticatedForumRepository;
import acme.framework.components.Model;
import acme.framework.components.Request;
import acme.framework.entities.Authenticated;
import acme.framework.entities.Principal;
import acme.framework.services.AbstractListService;

@Service
public class AuthenticatedMessageListService implements AbstractListService<Authenticated, Message> {

	// Internal state ---------------------------------------------------------

	@Autowired
	AuthenticatedMessageRepository	repository;

	@Autowired
	AuthenticatedForumRepository	forumRepository;


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
	public void unbind(final Request<Message> request, final Message entity, final Model model) {
		assert request != null;
		assert entity != null;
		assert model != null;

		request.unbind(entity, model, "title", "moment");
	}

	@Override
	public Collection<Message> findMany(final Request<Message> request) {
		assert request != null;

		Collection<Message> result;

		int id = request.getModel().getInteger("id");
		result = this.repository.findManyByForumId(id);

		return result;
	}
}
