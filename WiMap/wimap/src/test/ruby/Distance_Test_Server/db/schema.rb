# encoding: UTF-8
# This file is auto-generated from the current state of the database. Instead
# of editing this file, please use the migrations feature of Active Record to
# incrementally modify your database, and then regenerate this schema definition.
#
# Note that this schema.rb definition is the authoritative source for your
# database schema. If you need to create the application database on another
# system, you should be using db:schema:load, not running all the migrations
# from scratch. The latter is a flawed and unsustainable approach (the more migrations
# you'll amass, the slower it'll run and the greater likelihood for issues).
#
# It's strongly recommended that you check this file into your version control system.

ActiveRecord::Schema.define(version: 20140214193840) do

  # These are extensions that must be enabled in order to support this database
  enable_extension "plpgsql"

  create_table "beacons", force: true do |t|
    t.string   "name"
    t.string   "uid"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "distance_samples", force: true do |t|
    t.integer  "power"
    t.integer  "distance"
    t.integer  "beacon_id"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

end
