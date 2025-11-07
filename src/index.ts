import applicationCtrl from "./modules/controllers/application";
import * as historyCtrl from "./modules/controllers/history";
import * as keyboardController from "./modules/controllers/keyboard";
import * as scrollController from "./modules/controllers/scroll"

// Initialize Controllers
applicationCtrl.init();
historyCtrl.init();
keyboardController.init();
scrollController.init();
