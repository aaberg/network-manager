function toggleMenu() {
    const menu = document.querySelector('.menu');
    const menuOverlay = document.querySelector('.menu-overlay');
    if (!document.startViewTransition) {
        menu.classList.toggle('active');
        menuOverlay.classList.toggle('active');
        return;
    }

    document.startViewTransition(() => {
        menu.classList.toggle('active');
        menuOverlay.classList.toggle('active');
    });
}

function toggleListItem(event, selector) {
    document.querySelectorAll(selector)
        .forEach(element => element.classList.remove('selected'));

    event.currentTarget.classList.add('selected');
}