
package acme.features.bookkeeper.accounting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.accountings.Accounting;
import acme.entities.roles.Bookkeeper;
import acme.framework.components.Model;
import acme.framework.components.Request;
import acme.framework.services.AbstractShowService;

@Service
public class BookkeeperAccountingShowService implements AbstractShowService<Bookkeeper, Accounting> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private BookkeeperAccountingRepository repository;


	@Override
	public boolean authorise(final Request<Accounting> request) {
		assert request != null;

		Integer idAccounting = request.getModel().getInteger("id");
		if (idAccounting != null) {
			Accounting accounting = this.repository.findOneAccountingById(idAccounting);
			boolean propietario = accounting.getBookkeeper().getUserAccount().getId() == request.getPrincipal().getAccountId();
			boolean published = accounting.isStatus();

			return propietario || published;
		}
		return true;
	}

	@Override
	public void unbind(final Request<Accounting> request, final Accounting entity, final Model model) {
		assert request != null;
		assert entity != null;
		assert model != null;

		String bookkeeperUsername = entity.getBookkeeper().getUserAccount().getUsername();
		model.setAttribute("bookkeeperUsername", bookkeeperUsername);

		String roundTicker = entity.getRound().getTicker();
		model.setAttribute("roundTicker", roundTicker);

		request.unbind(entity, model, "title", "creation", "status", "body", "bookkeeper", "round");
	}

	@Override
	public Accounting findOne(final Request<Accounting> request) {
		assert request != null;

		Accounting result;
		int id;

		id = request.getModel().getInteger("id");
		result = this.repository.findOneAccountingById(id);

		return result;
	}
}
