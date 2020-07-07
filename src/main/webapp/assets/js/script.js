/**
 * Shows all HTML elements of the given Class
 */
function showClass(className) {
    const classElements = document.getElementsByClassName(className);
    for(let i = 0; i < classElements["length"]; i++){
        classElements[i].style.display = "block";
    }
}

/**
 * function that returns after the set number of milliseconds
 */
function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

/**
 * Defines getCurrentUser.cachedUser by fetching the current user from UserInformationServlet.
 * If the user is not logged in, the fetch will have a 400 response code and the cachedUser will be defined as null
 * Otherwise the cachedUser will be defined as the current User object
 */
async function defineCurrentUserCache() {
    const response = await fetch('/userInfo');
    if(response.ok){
        getCurrentUser.cachedUser = await response.json();
    } else {
        getCurrentUser.cachedUser = null;
    }
}

/**
 * Returns the Current Logged in user object
 * If no user is logged in, return null
 */
async function getCurrentUser() {
    if(getCurrentUser.cachedUser === undefined){
        await defineCurrentUserCache();
    }
    return getCurrentUser.cachedUser;
}

async function isUserLoggedIn() {
    const currentUser = await getCurrentUser();
    return currentUser != null;
}

/**
 * If the current user is logged in, unhide the show-logged-in class
 * If the current user is not logged in, unhide the show-logged-out class
 */
// TODO find better way to wait for content to exist
async function showLoginBasedContent() {
    await sleep(100); // avoid showing content until content exists
    if(await isUserLoggedIn()) {
        showClass("show-logged-in");
    } else {
        showClass("show-logged-out");
    }
}
