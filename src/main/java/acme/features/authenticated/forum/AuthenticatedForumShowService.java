
package acme.features.authenticated.forum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.forums.Forum;
import acme.framework.components.Model;
import acme.framework.components.Request;
import acme.framework.entities.Authenticated;
import acme.framework.entities.Principal;
import acme.framework.services.AbstractShowService;

@Service
public class AuthenticatedForumShowService implements AbstractShowService<Authenticated, Forum> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AuthenticatedForumRepository repository;


	@Override
	public boolean authorise(final Request<Forum> request) {
		assert request != null;

		Integer id = request.getModel().getInteger("id");

		if (id != null) {
			Principal principal = request.getPrincipal();
			boolean res = this.repository.findManyForumsByUserId(principal.getAccountId()).stream().anyMatch(f -> f.getId() == id.intValue());
			return res;
		}
		return true;
	}

	@Override
	public void unbind(final Request<Forum> request, final Forum entity, final Model model) {
		assert request != null;
		assert entity != null;
		assert model != null;

		String tickerRound = entity.getRound().getTicker();
		String nameRound = entity.getRound().getTitle();
		model.setAttribute("tickerRound", tickerRound);
		model.setAttribute("nameRound", nameRound);

		String direccion = "../message/list_by_forum?id=" + entity.getId();
		model.setAttribute("direccion", direccion);
		request.unbind(entity, model);

		String direccion2 = "../message/create?id=" + entity.getId();
		model.setAttribute("forumCreateMessage", direccion2);

	}

	@Override
	public Forum findOne(final Request<Forum> request) {
		assert request != null;

		Forum result;
		int id;

		id = request.getModel().getInteger("id");
		result = this.repository.findOneForumById(id);

		return result;
	}
}
