class CreateDistanceSamples < ActiveRecord::Migration
  def change
    create_table :distance_samples do |t|
      t.integer :power
      t.integer :distance
      t.integer :beacon_id

      t.timestamps
    end
  end
end
