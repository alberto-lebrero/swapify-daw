(function () {
  const overlay = document.createElement('div');
  overlay.className = 'modal-overlay';
  overlay.innerHTML = `
    <div class="modal">
      <p class="modal__msg"></p>
      <div class="modal__actions">
        <button class="btn-danger modal__confirm">Eliminar</button>
        <button class="btn-cancel modal__cancel">Cancelar</button>
      </div>
    </div>`;
  document.body.appendChild(overlay);

  const msg        = overlay.querySelector('.modal__msg');
  const confirmBtn = overlay.querySelector('.modal__confirm');
  const cancelBtn  = overlay.querySelector('.modal__cancel');

  function closeModal() { overlay.classList.remove('modal-overlay--open'); }

  cancelBtn.addEventListener('click', closeModal);
  overlay.addEventListener('click', e => { if (e.target === overlay) closeModal(); });
  document.addEventListener('keydown', e => { if (e.key === 'Escape') closeModal(); });

  document.querySelectorAll('form[data-confirm]').forEach(form => {
    form.addEventListener('submit', function (e) {
      if (this.dataset.ready) return;
      e.preventDefault();
      msg.textContent = this.dataset.confirm;
      overlay.classList.add('modal-overlay--open');

      confirmBtn.onclick = () => {
        closeModal();
        form.dataset.ready = 'true';
        form.submit();
      };
    });
  });
})();
