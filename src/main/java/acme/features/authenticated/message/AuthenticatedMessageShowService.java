
package acme.features.authenticated.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.messages.Message;
import acme.features.authenticated.forum.AuthenticatedForumRepository;
import acme.framework.components.Model;
import acme.framework.components.Request;
import acme.framework.entities.Authenticated;
import acme.framework.entities.Principal;
import acme.framework.services.AbstractShowService;

@Service
public class AuthenticatedMessageShowService implements AbstractShowService<Authenticated, Message> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AuthenticatedMessageRepository	repository;

	@Autowired
	private AuthenticatedForumRepository	forumRepository;


	@Override
	public boolean authorise(final Request<Message> request) {
		assert request != null;

		Integer messageId = request.getModel().getInteger("id");

		if (messageId != null) {
			Message m = this.repository.findOneMessageById(messageId);
			Principal principal = request.getPrincipal();
			boolean res = this.forumRepository.findManyForumsByUserId(principal.getAccountId()).stream().anyMatch(f -> f.getId() == m.getForum().getId());
			return res;
		}

		return true;
	}

	@Override
	public void unbind(final Request<Message> request, final Message entity, final Model model) {
		assert request != null;
		assert entity != null;
		assert model != null;
		Message m1 = this.repository.findOneMessageById(request.getModel().getInteger("id"));
		model.setAttribute("user", m1.getUser().getUserAccount().getUsername());
		request.unbind(entity, model, "title", "moment", "tags", "body");
	}

	@Override
	public Message findOne(final Request<Message> request) {
		assert request != null;

		Message result;
		int id;

		id = request.getModel().getInteger("id");
		result = this.repository.findOneMessageById(id);

		return result;
	}
}
