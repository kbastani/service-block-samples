#!/bin/bash

curl http://localhost:8787/v1/projects -X POST -d '{ "name": "Event sourcing example" }' -H "Content-Type: application/json"

curl http://localhost:8787/v1/projects/1/commits -X POST -d '{
	"status": "PUSHED",
	"files": [{
		"fileName": "Items.java",
		"status": "ADDED"
	},
	{
		"fileName": "Item.java",
		"status": "ADDED"
	}]
}' -H "Content-Type: application/json"