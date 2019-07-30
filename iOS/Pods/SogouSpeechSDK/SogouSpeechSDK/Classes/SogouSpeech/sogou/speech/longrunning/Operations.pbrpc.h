#if !defined(GPB_GRPC_FORWARD_DECLARE_MESSAGE_PROTO) || !GPB_GRPC_FORWARD_DECLARE_MESSAGE_PROTO
#import "sogou/speech/longrunning/Operations.pbobjc.h"
#endif

#if !defined(GPB_GRPC_PROTOCOL_ONLY) || !GPB_GRPC_PROTOCOL_ONLY
#import <ProtoRPC/ProtoService.h>
#import <ProtoRPC/ProtoRPC.h>
#import <RxLibrary/GRXWriteable.h>
#import <RxLibrary/GRXWriter.h>
#endif

@class CancelOperationRequest;
@class DeleteOperationRequest;
@class GPBEmpty;
@class GetOperationRequest;
@class ListOperationsRequest;
@class ListOperationsResponse;
@class Operation;

#if !defined(GPB_GRPC_FORWARD_DECLARE_MESSAGE_PROTO) || !GPB_GRPC_FORWARD_DECLARE_MESSAGE_PROTO
#if defined(GPB_USE_PROTOBUF_FRAMEWORK_IMPORTS) && GPB_USE_PROTOBUF_FRAMEWORK_IMPORTS
  #import <Protobuf/Any.pbobjc.h>
#else
  #import "google/protobuf/Any.pbobjc.h"
#endif
#if defined(GPB_USE_PROTOBUF_FRAMEWORK_IMPORTS) && GPB_USE_PROTOBUF_FRAMEWORK_IMPORTS
  #import <Protobuf/Empty.pbobjc.h>
#else
  #import "google/protobuf/Empty.pbobjc.h"
#endif
  #import "google/rpc/Status.pbobjc.h"
#endif

@class GRPCProtoCall;


NS_ASSUME_NONNULL_BEGIN

@protocol Operations <NSObject>

#pragma mark ListOperations(ListOperationsRequest) returns (ListOperationsResponse)

/**
 * Lists operations that match the specified filter in the request. If the
 * server doesn't support this method, it returns `UNIMPLEMENTED`.
 */
- (void)listOperationsWithRequest:(ListOperationsRequest *)request handler:(void(^)(ListOperationsResponse *_Nullable response, NSError *_Nullable error))handler;

/**
 * Lists operations that match the specified filter in the request. If the
 * server doesn't support this method, it returns `UNIMPLEMENTED`.
 */
- (GRPCProtoCall *)RPCToListOperationsWithRequest:(ListOperationsRequest *)request handler:(void(^)(ListOperationsResponse *_Nullable response, NSError *_Nullable error))handler;


#pragma mark GetOperation(GetOperationRequest) returns (Operation)

/**
 * Gets the latest state of a long-running operation.  Clients can use this
 * method to poll the operation result at intervals as recommended by the API
 * service.
 */
- (void)getOperationWithRequest:(GetOperationRequest *)request handler:(void(^)(Operation *_Nullable response, NSError *_Nullable error))handler;

/**
 * Gets the latest state of a long-running operation.  Clients can use this
 * method to poll the operation result at intervals as recommended by the API
 * service.
 */
- (GRPCProtoCall *)RPCToGetOperationWithRequest:(GetOperationRequest *)request handler:(void(^)(Operation *_Nullable response, NSError *_Nullable error))handler;


#pragma mark DeleteOperation(DeleteOperationRequest) returns (Empty)

/**
 * Deletes a long-running operation. This method indicates that the client is
 * no longer interested in the operation result. It does not cancel the
 * operation. If the server doesn't support this method, it returns
 * `google.rpc.Code.UNIMPLEMENTED`.
 */
- (void)deleteOperationWithRequest:(DeleteOperationRequest *)request handler:(void(^)(GPBEmpty *_Nullable response, NSError *_Nullable error))handler;

/**
 * Deletes a long-running operation. This method indicates that the client is
 * no longer interested in the operation result. It does not cancel the
 * operation. If the server doesn't support this method, it returns
 * `google.rpc.Code.UNIMPLEMENTED`.
 */
- (GRPCProtoCall *)RPCToDeleteOperationWithRequest:(DeleteOperationRequest *)request handler:(void(^)(GPBEmpty *_Nullable response, NSError *_Nullable error))handler;


#pragma mark CancelOperation(CancelOperationRequest) returns (Empty)

/**
 * Starts asynchronous cancellation on a long-running operation.  The server
 * makes a best effort to cancel the operation, but success is not
 * guaranteed.  If the server doesn't support this method, it returns
 * `google.rpc.Code.UNIMPLEMENTED`.  Clients can use
 * [Operations.GetOperation][sogou.speech.longrunning.Operations.GetOperation] or
 * other methods to check whether the cancellation succeeded or whether the
 * operation completed despite cancellation. On successful cancellation,
 * the operation is not deleted; instead, it becomes an operation with
 * an [Operation.error][sogou.speech.longrunning.Operation.error] value with a [google.rpc.Status.code][google.rpc.Status.code] of 1,
 * corresponding to `Code.CANCELLED`.
 */
- (void)cancelOperationWithRequest:(CancelOperationRequest *)request handler:(void(^)(GPBEmpty *_Nullable response, NSError *_Nullable error))handler;

/**
 * Starts asynchronous cancellation on a long-running operation.  The server
 * makes a best effort to cancel the operation, but success is not
 * guaranteed.  If the server doesn't support this method, it returns
 * `google.rpc.Code.UNIMPLEMENTED`.  Clients can use
 * [Operations.GetOperation][sogou.speech.longrunning.Operations.GetOperation] or
 * other methods to check whether the cancellation succeeded or whether the
 * operation completed despite cancellation. On successful cancellation,
 * the operation is not deleted; instead, it becomes an operation with
 * an [Operation.error][sogou.speech.longrunning.Operation.error] value with a [google.rpc.Status.code][google.rpc.Status.code] of 1,
 * corresponding to `Code.CANCELLED`.
 */
- (GRPCProtoCall *)RPCToCancelOperationWithRequest:(CancelOperationRequest *)request handler:(void(^)(GPBEmpty *_Nullable response, NSError *_Nullable error))handler;


@end


#if !defined(GPB_GRPC_PROTOCOL_ONLY) || !GPB_GRPC_PROTOCOL_ONLY
/**
 * Basic service implementation, over gRPC, that only does
 * marshalling and parsing.
 */
@interface Operations : GRPCProtoService<Operations>
- (instancetype)initWithHost:(NSString *)host NS_DESIGNATED_INITIALIZER;
+ (instancetype)serviceWithHost:(NSString *)host;
@end
#endif

NS_ASSUME_NONNULL_END

