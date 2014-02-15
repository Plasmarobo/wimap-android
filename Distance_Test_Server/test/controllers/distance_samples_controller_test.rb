require 'test_helper'

class DistanceSamplesControllerTest < ActionController::TestCase
  setup do
    @distance_sample = distance_samples(:one)
  end

  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:distance_samples)
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  test "should create distance_sample" do
    assert_difference('DistanceSample.count') do
      post :create, distance_sample: { integer: @distance_sample.integer, integer: @distance_sample.integer, integer: @distance_sample.integer }
    end

    assert_redirected_to distance_sample_path(assigns(:distance_sample))
  end

  test "should show distance_sample" do
    get :show, id: @distance_sample
    assert_response :success
  end

  test "should get edit" do
    get :edit, id: @distance_sample
    assert_response :success
  end

  test "should update distance_sample" do
    patch :update, id: @distance_sample, distance_sample: { integer: @distance_sample.integer, integer: @distance_sample.integer, integer: @distance_sample.integer }
    assert_redirected_to distance_sample_path(assigns(:distance_sample))
  end

  test "should destroy distance_sample" do
    assert_difference('DistanceSample.count', -1) do
      delete :destroy, id: @distance_sample
    end

    assert_redirected_to distance_samples_path
  end
end
