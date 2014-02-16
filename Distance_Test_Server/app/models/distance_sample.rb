class DistanceSample < ActiveRecord::Base
	belongs_to :beacon, dependent: :destroy
end
