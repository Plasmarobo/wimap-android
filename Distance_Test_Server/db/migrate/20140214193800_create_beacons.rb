class CreateBeacons < ActiveRecord::Migration
  def change
    create_table :beacons do |t|
      t.string :name
      t.string :uid

      t.timestamps
    end
  end
end
