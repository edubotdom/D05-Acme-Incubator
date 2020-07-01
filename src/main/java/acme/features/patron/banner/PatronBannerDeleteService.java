
package acme.features.patron.banner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.banners.Banner;
import acme.entities.roles.Patron;
import acme.features.patron.card.PatronCardRepository;
import acme.framework.components.Errors;
import acme.framework.components.Model;
import acme.framework.components.Request;
import acme.framework.services.AbstractDeleteService;

@Service
public class PatronBannerDeleteService implements AbstractDeleteService<Patron, Banner> {

	@Autowired
	PatronBannerRepository	repository;

	@Autowired
	PatronCardRepository	cardRepository;


	@Override
	public boolean authorise(final Request<Banner> request) {
		assert request != null;

		Integer bannerId = request.getModel().getInteger("id");
		Banner banner = this.repository.findOneById(bannerId);

		return banner.getPatron().getUserAccount().getId() == request.getPrincipal().getAccountId();
	}

	@Override
	public void bind(final Request<Banner> request, final Banner entity, final Errors errors) {
		assert request != null;
		assert entity != null;
		assert errors != null;

		request.bind(entity, errors);

	}

	@Override
	public void unbind(final Request<Banner> request, final Banner entity, final Model model) {
		assert request != null;
		assert entity != null;
		assert model != null;

		request.unbind(entity, model, "picture", "slogan", "url", "patron", "card");
	}

	@Override
	public Banner findOne(final Request<Banner> request) {
		assert request != null;
		Banner result;
		int id;
		id = request.getModel().getInteger("id");
		result = this.repository.findOneById(id);
		return result;
	}

	@Override
	public void validate(final Request<Banner> request, final Banner entity, final Errors errors) {
		assert request != null;
		assert entity != null;
		assert errors != null;
	}

	@Override
	public void delete(final Request<Banner> request, final Banner entity) {
		assert request != null;
		assert entity != null;

		//List<Banner> banners = new ArrayList<>(this.cardRepository.findBannersByCard(entity.getCard().getId()));
		this.repository.delete(entity);
		/*
		 * if (banners.size() < 2) {
		 * Card card = banners.get(0).getCard();
		 * this.cardRepository.delete(card);
		 * }
		 */
	}

}
