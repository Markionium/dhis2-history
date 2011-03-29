var validationRules = {
	/* dhis-web-maintenance-user */
	"user" : {
		"name" : {
			"rangelength" : [ 2, 140 ]
		},
		"username" : {
			"rangelength" : [ 2, 140 ],
			"firstletteralphabet" : true,
			"alphanumeric" : true
		},
		"password" : {
			"rangelength" : [ 8, 35 ]
		},
		"email" : {
			"rangelength" : [ 0, 160 ]
		},
		"phone" : {
			"rangelength" : [ 0, 80 ]
		}
	},
	"role" : {
		"name" : {
			"rangelength" : [ 2, 140 ]
		},
		"description" : {
			"rangelength" : [ 2, 210 ]
		}
	},
	"userGroup" : {
		"name" : {
			"rangelength" : [ 2, 210 ],
			"alphanumericwithbasicpuncspaces" : true,
			"firstletteralphabet" : true
		}
	},

	/* dhis-web-maintenance-organisationunit */
	"organisationUnit" : {
		"name" : {
			"rangelength" : [ 2, 160 ]
		},
		"shortName" : {
			"rangelength" : [ 2, 25 ]
		},
		"code" : {
			"rangelength" : [ 0, 25 ]
		},
		"url" : {
			"rangelength" : [ 0, 255 ]
		},
		"contactPerson" : {
			"rangelength" : [ 0, 255 ]
		},
		"address" : {
			"rangelength" : [ 0, 255 ]
		},
		"email" : {
			"rangelength" : [ 0, 250 ]
		},
		"phoneNumber" : {
			"rangelength" : [ 0, 255 ]
		}
	},
	"organisationUnitGroup" : {
		"name" : {
			"rangelength" : [ 2, 160 ]
		}
	},
	"organisationUnitGroupSet" : {
		"name" : {
			"rangelength" : [ 2, 230 ]
		},
		"description" : {
			"rangelength" : [ 2, 255 ]
		}
	},

	/* dhis-web-maintenance-dataset */
	"dataEntry" : {
		"name" : {
			"rangelength" : [ 4, 100 ]
		}
	},
	"section" : {
		"name" : {
			"rangelength" : [ 2, 160 ]
		},
		"selectedList" : {

		}
	},
	"dataSet" : {
		"name" : {
			"alphanumericwithbasicpuncspaces" : true,
			"firstletteralphabet" : false,
			"rangelength" : [ 4, 150 ]
		},
		"shortName" : {
			"alphanumericwithbasicpuncspaces" : true,
			"firstletteralphabet" : false,
			"rangelength" : [ 2, 20 ]
		},
		"code" : {
			"alphanumericwithbasicpuncspaces" : true,
			"notOnlyDigits" : false,
			"rangelength" : [ 4, 40 ]
		}
	},

	/* dhis-web-maintenance-dataadmin */
	"sqlView" : {
		"name" : {
			"rangelength" : [ 2, 50 ]
		},
		"description" : {
			"rangelength" : [ 2, 255 ]
		},
		"sqlquery" : {
			"rangelength" : [ 1, 255 ]
		}
	},
	"dataLocking" : {},
	"dataBrowser" : {},
	"minMax" : {},

	/* dhis-web-validationrule */
	"validationRule" : {
		"name" : {
			"rangelength" : [ 2, 160 ]
		},
		"description" : {
			"rangelength" : [ 2, 160 ]
		}
	},
	"validationRuleGroup" : {
		"name" : {
			"rangelength" : [ 2, 160 ]
		},
		"description" : {
			"rangelength" : [ 2, 160 ]
		}
	}
}
