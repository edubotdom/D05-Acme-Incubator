
package acme.features.bookkeeper.accounting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.accountings.Accounting;
import acme.entities.roles.Bookkeeper;
import acme.framework.components.Errors;
import acme.framework.components.Model;
import acme.framework.components.Request;
import acme.framework.services.AbstractUpdateService;

@Service
public class BookkeeperAccountingUpdateService implements AbstractUpdateService<Bookkeeper, Accounting> {

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

			return propietario && !published;
		}
		return true;
	}

	@Override
	public void bind(final Request<Accounting> request, final Accounting entity, final Errors errors) {
		assert request != null;
		assert entity != null;
		assert errors != null;

		request.bind(entity, errors);
	}

	@Override
	public void unbind(final Request<Accounting> request, final Accounting entity, final Model model) {
		assert request != null;
		assert entity != null;
		assert model != null;

		String createAccounting = "../accounting/create?id=" + request.getModel().getInteger("id");
		model.setAttribute("createAccounting", createAccounting);

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

	@Override
	public void validate(final Request<Accounting> request, final Accounting entity, final Errors errors) {
		assert request != null;
		assert entity != null;
		assert errors != null;

	}

	@Override
	public void update(final Request<Accounting> request, final Accounting entity) {

		this.repository.save(entity);

	}

}
