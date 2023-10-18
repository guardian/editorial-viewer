import * as applicationCtrl from "./modules/controllers/application";
import * as analyticsCtrl from "./modules/controllers/analytics";
import * as historyCtrl from "./modules/controllers/history";
import * as keyboardController from "./modules/controllers/keyboard";

//Initialize Controllers
applicationCtrl.init();
analyticsCtrl.init();
historyCtrl.init();
keyboardController.init();
